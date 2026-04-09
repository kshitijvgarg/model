package com.scholarship.dao;

import com.scholarship.model.Student;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class StudentDAO {

    public int insert(Student student) throws SQLException {
        String sql = """
            INSERT INTO students (name, roll_number, email, cgpa, family_income, 
                category, attendance_percent, backlog_count, date_of_birth, 
                gender, is_disabled, state, current_semester)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setString(1, student.getName());
            pstmt.setString(2, student.getRollNumber());
            pstmt.setString(3, student.getEmail());
            pstmt.setDouble(4, student.getCgpa());
            pstmt.setDouble(5, student.getFamilyIncome());
            pstmt.setString(6, student.getCategory());
            pstmt.setDouble(7, student.getAttendancePercent());
            pstmt.setInt(8, student.getBacklogCount());
            pstmt.setString(9, student.getDateOfBirth() != null ? 
                student.getDateOfBirth().toString() : null);
            pstmt.setString(10, student.getGender());
            pstmt.setInt(11, student.isDisabled() ? 1 : 0);
            pstmt.setString(12, student.getState());
            pstmt.setInt(13, student.getCurrentSemester());

            pstmt.executeUpdate();

            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        return -1;
    }

    public Optional<Student> findById(int id) throws SQLException {
        String sql = "SELECT * FROM students WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToStudent(rs));
                }
            }
        }
        return Optional.empty();
    }

    public Optional<Student> findByRollNumber(String rollNumber) throws SQLException {
        String sql = "SELECT * FROM students WHERE roll_number = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, rollNumber);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToStudent(rs));
                }
            }
        }
        return Optional.empty();
    }

    public List<Student> findAllActive() throws SQLException {

    List<Student> list = new ArrayList<>();
    String sql = "SELECT * FROM students";

    try (Connection conn = DatabaseConnection.getConnection();
         Statement stmt = conn.createStatement();
         ResultSet rs = stmt.executeQuery(sql)) {

        while (rs.next()) {
            Student s = new Student(
                rs.getString("name"),
                rs.getString("roll_number"),
                rs.getDouble("cgpa"),
                rs.getDouble("family_income"),
                rs.getString("category"),
                rs.getInt("attendance_percent")
            );
            list.add(s);
        }
    }

    return list;
}

    public void update(Student student) throws SQLException {
        String sql = """
            UPDATE students SET name = ?, email = ?, cgpa = ?, family_income = ?,
                category = ?, attendance_percent = ?, backlog_count = ?,
                date_of_birth = ?, gender = ?, is_disabled = ?, state = ?,
                current_semester = ?
            WHERE id = ?
            """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, student.getName());
            pstmt.setString(2, student.getEmail());
            pstmt.setDouble(3, student.getCgpa());
            pstmt.setDouble(4, student.getFamilyIncome());
            pstmt.setString(5, student.getCategory());
            pstmt.setDouble(6, student.getAttendancePercent());
            pstmt.setInt(7, student.getBacklogCount());
            pstmt.setString(8, student.getDateOfBirth() != null ? 
                student.getDateOfBirth().toString() : null);
            pstmt.setString(9, student.getGender());
            pstmt.setInt(10, student.isDisabled() ? 1 : 0);
            pstmt.setString(11, student.getState());
            pstmt.setInt(12, student.getCurrentSemester());
            pstmt.setInt(13, student.getId());

            pstmt.executeUpdate();
        }
    }

    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM students WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        }
    }

    private Student mapResultSetToStudent(ResultSet rs) throws SQLException {
        Student student = new Student();
        student.setId(rs.getInt("id"));
        student.setName(rs.getString("name"));
        student.setRollNumber(rs.getString("roll_number"));
        student.setEmail(rs.getString("email"));
        student.setCgpa(rs.getDouble("cgpa"));
        student.setFamilyIncome(rs.getDouble("family_income"));
        student.setCategory(rs.getString("category"));
        student.setAttendancePercent(rs.getDouble("attendance_percent"));
        student.setBacklogCount(rs.getInt("backlog_count"));
        
        String dob = rs.getString("date_of_birth");
        if (dob != null && !dob.isEmpty()) {
            student.setDateOfBirth(LocalDate.parse(dob));
        }
        
        student.setGender(rs.getString("gender"));
        student.setDisabled(rs.getInt("is_disabled") == 1);
        student.setState(rs.getString("state"));
        student.setCurrentSemester(rs.getInt("current_semester"));
        
        return student;
    }
}
