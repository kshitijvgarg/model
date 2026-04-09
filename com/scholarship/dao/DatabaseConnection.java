package com.scholarship.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseConnection {

    private static final String DB_URL = "jdbc:sqlite:scholarship.db";

    // ❗ NO global connection
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL);
    }

    public static void initializeDatabase() {

        String createStudentsTable = """
            CREATE TABLE IF NOT EXISTS students (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                name TEXT NOT NULL,
                roll_number TEXT UNIQUE NOT NULL,
                email TEXT,
                cgpa REAL NOT NULL,
                family_income REAL NOT NULL,
                category TEXT NOT NULL,
                attendance_percent REAL NOT NULL,
                backlog_count INTEGER DEFAULT 0,
                date_of_birth TEXT,
                gender TEXT,
                is_disabled INTEGER DEFAULT 0,
                state TEXT,
                current_semester INTEGER DEFAULT 1,
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
            )
            """;

        String createScholarshipsTable = """
            CREATE TABLE IF NOT EXISTS scholarships (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                name TEXT NOT NULL,
                description TEXT,
                amount REAL NOT NULL,
                provider TEXT NOT NULL,
                is_active INTEGER DEFAULT 1,
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
            )
            """;

        String createRulesTable = """
            CREATE TABLE IF NOT EXISTS eligibility_rules (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                scholarship_id INTEGER NOT NULL,
                rule_type TEXT NOT NULL,
                operator TEXT NOT NULL,
                value TEXT NOT NULL,
                description TEXT,
                is_mandatory INTEGER DEFAULT 1,
                FOREIGN KEY (scholarship_id) REFERENCES scholarships(id)
            )
            """;

        String createResultsTable = """
            CREATE TABLE IF NOT EXISTS eligibility_results (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                student_id INTEGER NOT NULL,
                scholarship_id INTEGER NOT NULL,
                is_eligible INTEGER NOT NULL,
                passed_rules TEXT,
                failed_rules TEXT,
                remarks TEXT,
                checked_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                FOREIGN KEY (student_id) REFERENCES students(id),
                FOREIGN KEY (scholarship_id) REFERENCES scholarships(id)
            )
            """;

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {

            stmt.execute(createStudentsTable);
            stmt.execute(createScholarshipsTable);
            stmt.execute(createRulesTable);
            stmt.execute(createResultsTable);

            System.out.println("Database initialized successfully.");

        } catch (SQLException e) {
            System.err.println("Database initialization failed: " + e.getMessage());
        }
    }
}