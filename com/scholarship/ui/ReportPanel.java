package com.scholarship.ui;

import com.scholarship.dao.ScholarshipDAO;
import com.scholarship.dao.StudentDAO;
import com.scholarship.model.*;
import com.scholarship.service.EligibilityService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class ReportPanel extends JPanel {

    private JTable table;
    private DefaultTableModel model;
    private StudentDAO studentDAO;
    private ScholarshipDAO scholarshipDAO;
    private EligibilityService eligibilityService;

    public ReportPanel() {
        studentDAO = new StudentDAO();
        scholarshipDAO = new ScholarshipDAO();
        eligibilityService = new EligibilityService();

        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel title = new JLabel("Eligibility Report", JLabel.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 20));
        add(title, BorderLayout.NORTH);

        String[] columns = {
            "Student Name", "Roll No", "Scholarship", "Eligible", "Passed", "Failed"
        };
        model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(model);
        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        JButton generateBtn = new JButton("Generate Report");
        JButton clearBtn = new JButton("Clear Report");

        generateBtn.addActionListener(e -> generateReport());
        clearBtn.addActionListener(e -> model.setRowCount(0));

        buttonPanel.add(generateBtn);
        buttonPanel.add(clearBtn);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void generateReport() {
        model.setRowCount(0);
        try {
            List<Student> students = studentDAO.findAllActive();
            List<Scholarship> scholarships = scholarshipDAO.findAllActive();

            for (Student student : students) {
                for (Scholarship scholarship : scholarships) {
                    EligibilityResult result = eligibilityService.checkEligibility(student, scholarship);
                    model.addRow(new Object[]{
                        student.getName(),
                        student.getRollNumber(),
                        scholarship.getName(),
                        result.isEligible() ? "Yes" : "No",
                        result.getPassedRules().size(),
                        result.getFailedRules().size()
                    });
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                "Error generating report: " + e.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
