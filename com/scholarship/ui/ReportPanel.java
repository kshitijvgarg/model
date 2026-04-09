package com.scholarship.ui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class ReportPanel extends JPanel {

    private JTable table;
    private DefaultTableModel model;

    public ReportPanel() {
        setLayout(new BorderLayout());

        JLabel title = new JLabel("Eligibility Report", JLabel.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 20));

        // Table Columns
        String[] columns = {
            "Student Name", "Marks", "Income", "Attendance", "Category", "Result"
        };

        model = new DefaultTableModel(columns, 0);
        table = new JTable(model);

        JScrollPane scrollPane = new JScrollPane(table);

        // Buttons
        JPanel buttonPanel = new JPanel();

        JButton loadButton = new JButton("Load Sample Data");
        JButton clearButton = new JButton("Clear Report");

        buttonPanel.add(loadButton);
        buttonPanel.add(clearButton);

        // ===== Load Sample Data =====
        loadButton.addActionListener(e -> {
            model.addRow(new Object[]{"Rishab", 85, 300000, 90, "OBC", "Eligible"});
            model.addRow(new Object[]{"Amit", 60, 600000, 70, "General", "Not Eligible"});
            model.addRow(new Object[]{"Priya", 92, 200000, 95, "SC", "Eligible"});
        });

        // ===== Clear Table =====
        clearButton.addActionListener(e -> {
            model.setRowCount(0);
        });

        add(title, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }
}