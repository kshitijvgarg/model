package com.scholarship.ui;

import com.scholarship.dao.StudentDAO;
import com.scholarship.model.Student;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class StudentPanel extends JPanel {
    
    private JTable studentTable;
    private DefaultTableModel tableModel;
    private StudentDAO studentDAO;
    
    // Form fields
    private JTextField nameField, rollField, emailField, cgpaField;
    private JTextField incomeField, attendanceField, backlogField, semesterField;
    private JComboBox<String> categoryCombo, genderCombo;
    private JCheckBox disabledCheck;

    public StudentPanel() {
        studentDAO = new StudentDAO();
        initializeUI();
        loadStudents();
    }

    private void initializeUI() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Form panel (left side)
        JPanel formPanel = createFormPanel();
        add(formPanel, BorderLayout.WEST);

        // Table panel (center)
        JPanel tablePanel = createTablePanel();
        add(tablePanel, BorderLayout.CENTER);

        // Button panel (bottom)
        JPanel buttonPanel = createButtonPanel();
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private JPanel createFormPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Student Information"));
        panel.setPreferredSize(new Dimension(350, 0));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Initialize fields
        nameField = new JTextField(15);
        rollField = new JTextField(15);
        emailField = new JTextField(15);
        cgpaField = new JTextField(15);
        incomeField = new JTextField(15);
        attendanceField = new JTextField(15);
        backlogField = new JTextField(15);
        semesterField = new JTextField(15);
        categoryCombo = new JComboBox<>(new String[]{"General", "OBC", "SC", "ST", "EWS"});
        genderCombo = new JComboBox<>(new String[]{"Male", "Female", "Other"});
        disabledCheck = new JCheckBox("Person with Disability");

        // Add components
        int row = 0;
        addFormRow(panel, gbc, row++, "Name*:", nameField);
        addFormRow(panel, gbc, row++, "Roll Number*:", rollField);
        addFormRow(panel, gbc, row++, "Email:", emailField);
        addFormRow(panel, gbc, row++, "CGPA (0-10)*:", cgpaField);
        addFormRow(panel, gbc, row++, "Family Income*:", incomeField);
        addFormRow(panel, gbc, row++, "Category*:", categoryCombo);
        addFormRow(panel, gbc, row++, "Attendance %*:", attendanceField);
        addFormRow(panel, gbc, row++, "Backlogs:", backlogField);
        addFormRow(panel, gbc, row++, "Current Semester:", semesterField);
        addFormRow(panel, gbc, row++, "Gender:", genderCombo);
        
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 2;
        panel.add(disabledCheck, gbc);

        return panel;
    }

    private void addFormRow(JPanel panel, GridBagConstraints gbc, 
                           int row, String label, JComponent field) {
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 1;
        panel.add(new JLabel(label), gbc);
        gbc.gridx = 1;
        panel.add(field, gbc);
    }

    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Students List"));

        String[] columns = {"ID", "Name", "Roll No", "CGPA", "Income", 
                           "Category", "Attendance", "Backlogs"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        studentTable = new JTable(tableModel);
        studentTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        studentTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                loadSelectedStudent();
            }
        });

        JScrollPane scrollPane = new JScrollPane(studentTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));

        JButton addBtn = new JButton("Add Student");
        JButton updateBtn = new JButton("Update");
        JButton deleteBtn = new JButton("Delete");
        JButton clearBtn = new JButton("Clear Form");

        addBtn.addActionListener(e -> addStudent());
        updateBtn.addActionListener(e -> updateStudent());
        deleteBtn.addActionListener(e -> deleteStudent());
        clearBtn.addActionListener(e -> clearForm());

        panel.add(addBtn);
        panel.add(updateBtn);
        panel.add(deleteBtn);
        panel.add(clearBtn);

        return panel;
    }

    private void loadStudents() {
        tableModel.setRowCount(0);
        try {
            List<Student> students = studentDAO.findAllActive();
            for (Student s : students) {
                tableModel.addRow(new Object[]{
                    s.getId(), s.getName(), s.getRollNumber(), 
                    s.getCgpa(), s.getFamilyIncome(), s.getCategory(),
                    s.getAttendancePercent(), s.getBacklogCount()
                });
            }
        } catch (SQLException e) {
            showError("Error loading students: " + e.getMessage());
        }
    }

    private void loadSelectedStudent() {
        int row = studentTable.getSelectedRow();
        if (row >= 0) {
            int id = (int) tableModel.getValueAt(row, 0);
            try {
                studentDAO.findById(id).ifPresent(s -> {
                    nameField.setText(s.getName());
                    rollField.setText(s.getRollNumber());
                    emailField.setText(s.getEmail());
                    cgpaField.setText(String.valueOf(s.getCgpa()));
                    incomeField.setText(String.valueOf(s.getFamilyIncome()));
                    categoryCombo.setSelectedItem(s.getCategory());
                    attendanceField.setText(String.valueOf(s.getAttendancePercent()));
                    backlogField.setText(String.valueOf(s.getBacklogCount()));
                    semesterField.setText(String.valueOf(s.getCurrentSemester()));
                    genderCombo.setSelectedItem(s.getGender());
                    disabledCheck.setSelected(s.isDisabled());
                });
            } catch (SQLException e) {
                showError("Error loading student: " + e.getMessage());
            }
        }
    }

    private void addStudent() {
        try {
            Student student = getStudentFromForm();
            if (student != null) {
                studentDAO.insert(student);
                loadStudents();
                clearForm();
                JOptionPane.showMessageDialog(this, "Student added successfully!");
            }
        } catch (SQLException e) {
            showError("Error adding student: " + e.getMessage());
        }
    }

    private void updateStudent() {
        int row = studentTable.getSelectedRow();
        if (row < 0) {
            showError("Please select a student to update.");
            return;
        }

        try {
            Student student = getStudentFromForm();
            if (student != null) {
                student.setId((int) tableModel.getValueAt(row, 0));
                studentDAO.update(student);
                loadStudents();
                JOptionPane.showMessageDialog(this, "Student updated successfully!");
            }
        } catch (SQLException e) {
            showError("Error updating student: " + e.getMessage());
        }
    }

    private void deleteStudent() {
        int row = studentTable.getSelectedRow();
        if (row < 0) {
            showError("Please select a student to delete.");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to delete this student?",
            "Confirm Delete", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                int id = (int) tableModel.getValueAt(row, 0);
                studentDAO.delete(id);
                loadStudents();
                clearForm();
                JOptionPane.showMessageDialog(this, "Student deleted successfully!");
            } catch (SQLException e) {
                showError("Error deleting student: " + e.getMessage());
            }
        }
    }

    private Student getStudentFromForm() {
        // Validation
        if (nameField.getText().trim().isEmpty() || 
            rollField.getText().trim().isEmpty()) {
            showError("Name and Roll Number are required.");
            return null;
        }

        try {
            Student student = new Student();
            student.setName(nameField.getText().trim());
            student.setRollNumber(rollField.getText().trim());
            student.setEmail(emailField.getText().trim());
            student.setCgpa(Double.parseDouble(cgpaField.getText().trim()));
            student.setFamilyIncome(Double.parseDouble(incomeField.getText().trim()));
            student.setCategory((String) categoryCombo.getSelectedItem());
            student.setAttendancePercent(Double.parseDouble(attendanceField.getText().trim()));
            student.setBacklogCount(backlogField.getText().isEmpty() ? 0 : 
                Integer.parseInt(backlogField.getText().trim()));
            student.setCurrentSemester(semesterField.getText().isEmpty() ? 1 : 
                Integer.parseInt(semesterField.getText().trim()));
            student.setGender((String) genderCombo.getSelectedItem());
            student.setDisabled(disabledCheck.isSelected());
            
            return student;
        } catch (NumberFormatException e) {
            showError("Please enter valid numeric values.");
            return null;
        }
    }

    private void clearForm() {
        nameField.setText("");
        rollField.setText("");
        emailField.setText("");
        cgpaField.setText("");
        incomeField.setText("");
        attendanceField.setText("");
        backlogField.setText("");
        semesterField.setText("");
        categoryCombo.setSelectedIndex(0);
        genderCombo.setSelectedIndex(0);
        disabledCheck.setSelected(false);
        studentTable.clearSelection();
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }
}
