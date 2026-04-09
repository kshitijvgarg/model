package com.scholarship.ui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class ScholarshipPanel extends JPanel {

    private JTextField nameField, minMarksField, maxIncomeField, minAttendanceField;
    private JComboBox<String> categoryBox;
    private JTable table;
    private DefaultTableModel model;

    public ScholarshipPanel() {
        setLayout(new BorderLayout());

        // ===== Form Panel =====
        JPanel formPanel = new JPanel(new GridLayout(6, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createTitledBorder("Add Scholarship"));

        nameField = new JTextField();
        minMarksField = new JTextField();
        maxIncomeField = new JTextField();
        minAttendanceField = new JTextField();

        categoryBox = new JComboBox<>(new String[]{"SC", "ST", "OBC", "General"});

        formPanel.add(new JLabel("Scholarship Name:"));
        formPanel.add(nameField);

        formPanel.add(new JLabel("Minimum Marks:"));
        formPanel.add(minMarksField);

        formPanel.add(new JLabel("Max Income:"));
        formPanel.add(maxIncomeField);

        formPanel.add(new JLabel("Minimum Attendance:"));
        formPanel.add(minAttendanceField);

        formPanel.add(new JLabel("Category:"));
        formPanel.add(categoryBox);

        JButton addButton = new JButton("Add Scholarship");
        formPanel.add(addButton);

        // ===== Table Panel =====
        String[] columns = {"Name", "Marks", "Income", "Attendance", "Category"};
        model = new DefaultTableModel(columns, 0);
        table = new JTable(model);

        JScrollPane scrollPane = new JScrollPane(table);

        // ===== Add Action =====
        addButton.addActionListener(e -> {
            String name = nameField.getText();
            String marks = minMarksField.getText();
            String income = maxIncomeField.getText();
            String attendance = minAttendanceField.getText();
            String category = categoryBox.getSelectedItem().toString();

            model.addRow(new Object[]{name, marks, income, attendance, category});

            JOptionPane.showMessageDialog(this, "Scholarship Added Successfully!");

            clearFields();
        });

        add(formPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
    }

    private void clearFields() {
        nameField.setText("");
        minMarksField.setText("");
        maxIncomeField.setText("");
        minAttendanceField.setText("");
    }
}