package com.scholarship.ui;

import com.scholarship.dao.ScholarshipDAO;
import com.scholarship.dao.StudentDAO;
import com.scholarship.model.*;
import com.scholarship.service.EligibilityService;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class EligibilityPanel extends JPanel {

    private JComboBox<Student> studentCombo;
    private JComboBox<Scholarship> scholarshipCombo;
    private JTextArea resultArea;
    private JButton checkSingleBtn, checkAllBtn;

    private StudentDAO studentDAO;
    private ScholarshipDAO scholarshipDAO;
    private EligibilityService eligibilityService;

    public EligibilityPanel() {
        studentDAO = new StudentDAO();
        scholarshipDAO = new ScholarshipDAO();
        eligibilityService = new EligibilityService();

        initializeUI();
        loadData(); // ✅ load data on start
    }

    private void initializeUI() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // ===== TOP PANEL =====
        JPanel selectionPanel = new JPanel(new GridBagLayout());
        selectionPanel.setBorder(BorderFactory.createTitledBorder("Selection"));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        studentCombo = new JComboBox<>();
        scholarshipCombo = new JComboBox<>();

        gbc.gridx = 0; gbc.gridy = 0;
        selectionPanel.add(new JLabel("Select Student:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        selectionPanel.add(studentCombo, gbc);

        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0;
        selectionPanel.add(new JLabel("Select Scholarship:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        selectionPanel.add(scholarshipCombo, gbc);

        add(selectionPanel, BorderLayout.NORTH);

        // ===== CENTER PANEL =====
        JPanel resultPanel = new JPanel(new BorderLayout());
        resultPanel.setBorder(BorderFactory.createTitledBorder("Eligibility Results"));

        resultArea = new JTextArea();
        resultArea.setEditable(false);
        resultArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 14));
        resultArea.setMargin(new Insets(10, 10, 10, 10));

        resultPanel.add(new JScrollPane(resultArea), BorderLayout.CENTER);
        add(resultPanel, BorderLayout.CENTER);

        // ===== BUTTON PANEL =====
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));

        checkSingleBtn = new JButton("Check Selected");
        checkAllBtn = new JButton("Check All Scholarships for Student");
        JButton clearBtn = new JButton("Clear Results");

        checkSingleBtn.addActionListener(e -> checkSingleEligibility());
        checkAllBtn.addActionListener(e -> checkAllScholarships());
        clearBtn.addActionListener(e -> resultArea.setText(""));

        buttonPanel.add(checkSingleBtn);
        buttonPanel.add(checkAllBtn);
        buttonPanel.add(clearBtn);

        // Auto-refresh dropdowns when this panel becomes visible
        addHierarchyListener(e -> {
            if (isShowing()) loadData();
        });

        add(buttonPanel, BorderLayout.SOUTH);
    }

    // ✅ FIXED METHOD
    private void loadData() {
        try {
            studentCombo.removeAllItems();       // clear old
            scholarshipCombo.removeAllItems();   // clear old

            // ✅ FIX: use findAll() (not findAllActive)
            List<Student> students = studentDAO.findAllActive();
            for (Student s : students) {
                studentCombo.addItem(s);
            }

            List<Scholarship> scholarships = scholarshipDAO.findAllActive();
            for (Scholarship s : scholarships) {
                scholarshipCombo.addItem(s);
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Error loading data: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void checkSingleEligibility() {
        Student student = (Student) studentCombo.getSelectedItem();
        Scholarship scholarship = (Scholarship) scholarshipCombo.getSelectedItem();

        if (student == null || scholarship == null) {
            JOptionPane.showMessageDialog(this,
                    "Please select both a student and a scholarship.",
                    "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        EligibilityResult result = eligibilityService.checkEligibility(student, scholarship);
        displayResult(result);
    }

    private void checkAllScholarships() {
        Student student = (Student) studentCombo.getSelectedItem();

        if (student == null) {
            JOptionPane.showMessageDialog(this,
                    "Please select a student.",
                    "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            List<EligibilityResult> results = eligibilityService.checkAllScholarships(student);
            displayAllResults(student, results);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Error checking eligibility: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void displayResult(EligibilityResult result) {
        StringBuilder sb = new StringBuilder();
        sb.append("════════════════════════════════════════════════════════════\n");
        sb.append("         ELIGIBILITY CHECK RESULT\n");
        sb.append("════════════════════════════════════════════════════════════\n\n");
        sb.append(result.getSummary());
        sb.append("\n════════════════════════════════════════════════════════════");

        resultArea.setText(sb.toString());
    }

    private void displayAllResults(Student student, List<EligibilityResult> results) {
        StringBuilder sb = new StringBuilder();

        sb.append("════════════════════════════════════════════════════════════\n");
        sb.append("    ELIGIBILITY REPORT FOR: ").append(student.getName()).append("\n");
        sb.append("════════════════════════════════════════════════════════════\n\n");

        long eligible = results.stream().filter(EligibilityResult::isEligible).count();

        sb.append("Eligible for ").append(eligible)
          .append(" out of ").append(results.size()).append(" scholarships\n\n");

        for (EligibilityResult r : results) {
            sb.append(r.isEligible() ? "✓ " : "✗ ");
            sb.append(r.getScholarship().getName()).append("\n");
        }

        resultArea.setText(sb.toString());
    }
}