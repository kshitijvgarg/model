package com.scholarship.ui;

import com.scholarship.dao.DatabaseConnection;
import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {
    
    private JTabbedPane tabbedPane;
    private StudentPanel studentPanel;
    private ScholarshipPanel scholarshipPanel;
    private EligibilityPanel eligibilityPanel;
    private ReportPanel reportPanel;

    public MainFrame() {
        initializeUI();
         try {
        com.scholarship.util.SampleDataInitializer.initializeSampleData();
    } catch (Exception e) {
        e.printStackTrace();
    }
    }

    private void initializeUI() {
        setTitle("Scholarship Eligibility Automation System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 800);
        setLocationRelativeTo(null);

        // Initialize database
        DatabaseConnection.initializeDatabase();

        // Create tabbed pane
        tabbedPane = new JTabbedPane();
        
        studentPanel = new StudentPanel();
        scholarshipPanel = new ScholarshipPanel();
        eligibilityPanel = new EligibilityPanel();
        reportPanel = new ReportPanel();

        tabbedPane.addTab("👤 Students", studentPanel);
        tabbedPane.addTab("🎓 Scholarships", scholarshipPanel);
        tabbedPane.addTab("✓ Check Eligibility", eligibilityPanel);
        tabbedPane.addTab("📊 Reports", reportPanel);

        add(tabbedPane, BorderLayout.CENTER);

        // Add menu bar
        setJMenuBar(createMenuBar());

        // Handle window closing
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                
            }
        });
    }

    private JMenuBar createMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        // File menu
        JMenu fileMenu = new JMenu("File");
        JMenuItem exitItem = new JMenuItem("Exit");
        exitItem.addActionListener(e -> {
            
            System.exit(0);
        });
        fileMenu.add(exitItem);

        // Help menu
        JMenu helpMenu = new JMenu("Help");
        JMenuItem aboutItem = new JMenuItem("About");
        aboutItem.addActionListener(e -> showAboutDialog());
        helpMenu.add(aboutItem);

        menuBar.add(fileMenu);
        menuBar.add(helpMenu);

        return menuBar;
    }

    private void showAboutDialog() {
        JOptionPane.showMessageDialog(this,
            """
            Scholarship Eligibility Automation System
            Version 1.0
            
            An automated system for determining student 
            eligibility for various scholarships.
            
            © 2024 EdTech Solutions
            """,
            "About",
            JOptionPane.INFORMATION_MESSAGE);
    }
}
