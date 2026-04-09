package com.scholarship.ui;

import com.scholarship.dao.ScholarshipDAO;
import com.scholarship.model.EligibilityRule;
import com.scholarship.model.EligibilityRule.Operator;
import com.scholarship.model.EligibilityRule.RuleType;
import com.scholarship.model.Scholarship;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class ScholarshipPanel extends JPanel {

    private JTextField nameField, amountField, providerField, descriptionField;
    private JTextField minCgpaField, maxIncomeField, minAttendanceField, maxBacklogsField;
    private JComboBox<String> categoryCombo;
    private JTable table;
    private DefaultTableModel model;
    private ScholarshipDAO scholarshipDAO;

    public ScholarshipPanel() {
        scholarshipDAO = new ScholarshipDAO();
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        add(createFormPanel(), BorderLayout.WEST);
        add(createTablePanel(), BorderLayout.CENTER);
        add(createButtonPanel(), BorderLayout.SOUTH);

        loadScholarships();
    }

    private JPanel createFormPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Scholarship Information"));
        panel.setPreferredSize(new Dimension(350, 0));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        nameField = new JTextField(15);
        amountField = new JTextField(15);
        providerField = new JTextField(15);
        descriptionField = new JTextField(15);
        minCgpaField = new JTextField(15);
        maxIncomeField = new JTextField(15);
        minAttendanceField = new JTextField(15);
        maxBacklogsField = new JTextField(15);
        categoryCombo = new JComboBox<>(new String[]{"Any", "General", "OBC", "SC", "ST", "EWS", "SC,ST", "SC,ST,OBC"});

        int row = 0;
        addFormRow(panel, gbc, row++, "Name*:", nameField);
        addFormRow(panel, gbc, row++, "Amount*:", amountField);
        addFormRow(panel, gbc, row++, "Provider*:", providerField);
        addFormRow(panel, gbc, row++, "Description:", descriptionField);

        // Separator
        gbc.gridx = 0; gbc.gridy = row++; gbc.gridwidth = 2;
        panel.add(new JSeparator(), gbc);
        gbc.gridwidth = 1;

        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 2;
        JLabel rulesLabel = new JLabel("Eligibility Rules (leave blank to skip)");
        rulesLabel.setFont(rulesLabel.getFont().deriveFont(Font.ITALIC));
        panel.add(rulesLabel, gbc);
        gbc.gridwidth = 1;
        row++;

        addFormRow(panel, gbc, row++, "Min CGPA:", minCgpaField);
        addFormRow(panel, gbc, row++, "Max Income:", maxIncomeField);
        addFormRow(panel, gbc, row++, "Min Attendance %:", minAttendanceField);
        addFormRow(panel, gbc, row++, "Max Backlogs:", maxBacklogsField);
        addFormRow(panel, gbc, row++, "Category:", categoryCombo);

        return panel;
    }

    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Scholarships List"));

        String[] columns = {"ID", "Name", "Amount", "Provider", "Rules"};
        model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(model);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) loadSelectedScholarship();
        });

        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        return panel;
    }

    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));

        JButton addBtn = new JButton("Add Scholarship");
        JButton updateBtn = new JButton("Update");
        JButton deleteBtn = new JButton("Delete");
        JButton clearBtn = new JButton("Clear Form");

        addBtn.addActionListener(e -> addScholarship());
        updateBtn.addActionListener(e -> updateScholarship());
        deleteBtn.addActionListener(e -> deleteScholarship());
        clearBtn.addActionListener(e -> clearForm());

        panel.add(addBtn);
        panel.add(updateBtn);
        panel.add(deleteBtn);
        panel.add(clearBtn);

        return panel;
    }

    private void loadScholarships() {
        model.setRowCount(0);
        try {
            List<Scholarship> scholarships = scholarshipDAO.findAllActive();
            for (Scholarship s : scholarships) {
                model.addRow(new Object[]{
                    s.getId(), s.getName(), s.getAmount(), s.getProvider(),
                    s.getRules().size() + " rules"
                });
            }
        } catch (SQLException e) {
            showError("Error loading scholarships: " + e.getMessage());
        }
    }

    private void loadSelectedScholarship() {
        int row = table.getSelectedRow();
        if (row < 0) return;

        int id = (int) model.getValueAt(row, 0);
        try {
            scholarshipDAO.findById(id).ifPresent(s -> {
                nameField.setText(s.getName());
                amountField.setText(String.valueOf(s.getAmount()));
                providerField.setText(s.getProvider());
                descriptionField.setText(s.getDescription() != null ? s.getDescription() : "");

                // Clear rule fields
                minCgpaField.setText("");
                maxIncomeField.setText("");
                minAttendanceField.setText("");
                maxBacklogsField.setText("");
                categoryCombo.setSelectedIndex(0);

                // Populate rule fields from existing rules
                for (EligibilityRule r : s.getRules()) {
                    switch (r.getRuleType()) {
                        case MIN_CGPA -> minCgpaField.setText(r.getValue());
                        case MAX_INCOME -> maxIncomeField.setText(r.getValue());
                        case MIN_ATTENDANCE -> minAttendanceField.setText(r.getValue());
                        case MAX_BACKLOGS -> maxBacklogsField.setText(r.getValue());
                        case CATEGORY -> {
                            for (int i = 0; i < categoryCombo.getItemCount(); i++) {
                                if (categoryCombo.getItemAt(i).equalsIgnoreCase(r.getValue())) {
                                    categoryCombo.setSelectedIndex(i);
                                    break;
                                }
                            }
                        }
                        default -> {}
                    }
                }
            });
        } catch (SQLException e) {
            showError("Error loading scholarship: " + e.getMessage());
        }
    }

    private void addScholarship() {
        if (nameField.getText().trim().isEmpty() ||
            amountField.getText().trim().isEmpty() ||
            providerField.getText().trim().isEmpty()) {
            showError("Name, Amount, and Provider are required.");
            return;
        }

        try {
            Scholarship s = new Scholarship(
                nameField.getText().trim(),
                Double.parseDouble(amountField.getText().trim()),
                providerField.getText().trim()
            );
            s.setDescription(descriptionField.getText().trim());

            // Build rules from fields
            if (!minCgpaField.getText().trim().isEmpty()) {
                s.addRule(new EligibilityRule(RuleType.MIN_CGPA, Operator.GREATER_EQUAL,
                    minCgpaField.getText().trim()));
            }
            if (!maxIncomeField.getText().trim().isEmpty()) {
                s.addRule(new EligibilityRule(RuleType.MAX_INCOME, Operator.LESS_EQUAL,
                    maxIncomeField.getText().trim()));
            }
            if (!minAttendanceField.getText().trim().isEmpty()) {
                s.addRule(new EligibilityRule(RuleType.MIN_ATTENDANCE, Operator.GREATER_EQUAL,
                    minAttendanceField.getText().trim()));
            }
            if (!maxBacklogsField.getText().trim().isEmpty()) {
                s.addRule(new EligibilityRule(RuleType.MAX_BACKLOGS, Operator.LESS_EQUAL,
                    maxBacklogsField.getText().trim()));
            }
            String cat = (String) categoryCombo.getSelectedItem();
            if (!"Any".equals(cat)) {
                s.addRule(new EligibilityRule(RuleType.CATEGORY, Operator.IN_LIST, cat));
            }

            scholarshipDAO.insert(s);
            loadScholarships();
            clearForm();
            JOptionPane.showMessageDialog(this, "Scholarship added successfully!");
        } catch (NumberFormatException e) {
            showError("Please enter valid numeric values.");
        } catch (SQLException e) {
            showError("Error adding scholarship: " + e.getMessage());
        }
    }

    private void updateScholarship() {
        int row = table.getSelectedRow();
        if (row < 0) {
            showError("Please select a scholarship to update.");
            return;
        }

        if (nameField.getText().trim().isEmpty() ||
            amountField.getText().trim().isEmpty() ||
            providerField.getText().trim().isEmpty()) {
            showError("Name, Amount, and Provider are required.");
            return;
        }

        try {
            Scholarship s = new Scholarship(
                nameField.getText().trim(),
                Double.parseDouble(amountField.getText().trim()),
                providerField.getText().trim()
            );
            s.setId((int) model.getValueAt(row, 0));
            s.setDescription(descriptionField.getText().trim());

            if (!minCgpaField.getText().trim().isEmpty()) {
                s.addRule(new EligibilityRule(RuleType.MIN_CGPA, Operator.GREATER_EQUAL,
                    minCgpaField.getText().trim()));
            }
            if (!maxIncomeField.getText().trim().isEmpty()) {
                s.addRule(new EligibilityRule(RuleType.MAX_INCOME, Operator.LESS_EQUAL,
                    maxIncomeField.getText().trim()));
            }
            if (!minAttendanceField.getText().trim().isEmpty()) {
                s.addRule(new EligibilityRule(RuleType.MIN_ATTENDANCE, Operator.GREATER_EQUAL,
                    minAttendanceField.getText().trim()));
            }
            if (!maxBacklogsField.getText().trim().isEmpty()) {
                s.addRule(new EligibilityRule(RuleType.MAX_BACKLOGS, Operator.LESS_EQUAL,
                    maxBacklogsField.getText().trim()));
            }
            String cat = (String) categoryCombo.getSelectedItem();
            if (!"Any".equals(cat)) {
                s.addRule(new EligibilityRule(RuleType.CATEGORY, Operator.IN_LIST, cat));
            }

            scholarshipDAO.update(s);
            loadScholarships();
            clearForm();
            JOptionPane.showMessageDialog(this, "Scholarship updated successfully!");
        } catch (NumberFormatException e) {
            showError("Please enter valid numeric values.");
        } catch (SQLException e) {
            showError("Error updating scholarship: " + e.getMessage());
        }
    }

    private void deleteScholarship() {
        int row = table.getSelectedRow();
        if (row < 0) {
            showError("Please select a scholarship to delete.");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to delete this scholarship?",
            "Confirm Delete", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                int id = (int) model.getValueAt(row, 0);
                scholarshipDAO.delete(id);
                loadScholarships();
                clearForm();
                JOptionPane.showMessageDialog(this, "Scholarship deleted successfully!");
            } catch (SQLException e) {
                showError("Error deleting scholarship: " + e.getMessage());
            }
        }
    }

    private void clearForm() {
        nameField.setText("");
        amountField.setText("");
        providerField.setText("");
        descriptionField.setText("");
        minCgpaField.setText("");
        maxIncomeField.setText("");
        minAttendanceField.setText("");
        maxBacklogsField.setText("");
        categoryCombo.setSelectedIndex(0);
        table.clearSelection();
    }

    private void addFormRow(JPanel panel, GridBagConstraints gbc,
                            int row, String label, JComponent field) {
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 1;
        panel.add(new JLabel(label), gbc);
        gbc.gridx = 1;
        panel.add(field, gbc);
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }
}
