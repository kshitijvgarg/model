package com.scholarship.util;

import com.scholarship.dao.DatabaseConnection;
import com.scholarship.dao.ScholarshipDAO;
import com.scholarship.dao.StudentDAO;
import com.scholarship.model.*;
import com.scholarship.model.EligibilityRule.Operator;
import com.scholarship.model.EligibilityRule.RuleType;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class SampleDataInitializer {

    public static void initializeSampleData() throws Exception {
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM students")) {
            if (rs.next() && rs.getInt(1) > 0) {
                return;
            }
        }

        StudentDAO studentDAO = new StudentDAO();
        ScholarshipDAO scholarshipDAO = new ScholarshipDAO();

        // ===== 5 Students =====

        Student s1 = new Student("Aarav Mehta", "2024CS001", 9.1, 180000, "General", 94);
        s1.setEmail("aarav.mehta@gmail.com");
        s1.setCurrentSemester(5);
        s1.setGender("Male");
        s1.setState("Maharashtra");
        studentDAO.insert(s1);

        Student s2 = new Student("Diya Iyer", "2024CS002", 8.7, 120000, "OBC", 96);
        s2.setEmail("diya.iyer@gmail.com");
        s2.setCurrentSemester(5);
        s2.setGender("Female");
        s2.setState("Tamil Nadu");
        studentDAO.insert(s2);

        Student s3 = new Student("Karan Verma", "2024CS003", 7.4, 350000, "SC", 82);
        s3.setEmail("karan.verma@gmail.com");
        s3.setCurrentSemester(3);
        s3.setGender("Male");
        s3.setBacklogCount(2);
        s3.setState("Uttar Pradesh");
        studentDAO.insert(s3);

        Student s4 = new Student("Ananya Reddy", "2024CS004", 8.9, 90000, "ST", 91);
        s4.setEmail("ananya.reddy@gmail.com");
        s4.setCurrentSemester(5);
        s4.setGender("Female");
        s4.setState("Telangana");
        studentDAO.insert(s4);

        Student s5 = new Student("Rohan Joshi", "2024CS005", 6.8, 480000, "General", 74);
        s5.setEmail("rohan.joshi@gmail.com");
        s5.setCurrentSemester(3);
        s5.setGender("Male");
        s5.setBacklogCount(3);
        s5.setState("Rajasthan");
        studentDAO.insert(s5);

        // ===== 5 Scholarships =====

        Scholarship merit = new Scholarship("INSPIRE Scholarship", 80000, "DST, Govt of India");
        merit.setDescription("For top academic performers in science and technology");
        merit.addRule(new EligibilityRule(RuleType.MIN_CGPA, Operator.GREATER_EQUAL, "8.5"));
        merit.addRule(new EligibilityRule(RuleType.MIN_ATTENDANCE, Operator.GREATER_EQUAL, "90"));
        merit.addRule(new EligibilityRule(RuleType.MAX_BACKLOGS, Operator.LESS_EQUAL, "0"));
        scholarshipDAO.insert(merit);

        Scholarship postMatric = new Scholarship("Post Matric SC/ST Scholarship", 50000, "Ministry of Social Justice");
        postMatric.setDescription("Financial support for SC/ST students pursuing higher education");
        postMatric.addRule(new EligibilityRule(RuleType.CATEGORY, Operator.IN_LIST, "SC,ST"));
        postMatric.addRule(new EligibilityRule(RuleType.MIN_CGPA, Operator.GREATER_EQUAL, "6.0"));
        postMatric.addRule(new EligibilityRule(RuleType.MAX_INCOME, Operator.LESS_EQUAL, "300000"));
        scholarshipDAO.insert(postMatric);

        Scholarship centralSector = new Scholarship("Central Sector Scheme", 20000, "MHRD, Govt of India");
        centralSector.setDescription("For economically weaker students with good academics");
        centralSector.addRule(new EligibilityRule(RuleType.MAX_INCOME, Operator.LESS_EQUAL, "250000"));
        centralSector.addRule(new EligibilityRule(RuleType.MIN_CGPA, Operator.GREATER_EQUAL, "7.0"));
        centralSector.addRule(new EligibilityRule(RuleType.MIN_ATTENDANCE, Operator.GREATER_EQUAL, "75"));
        scholarshipDAO.insert(centralSector);

        Scholarship pragati = new Scholarship("Pragati Scholarship for Girls", 30000, "AICTE");
        pragati.setDescription("To encourage girl students in technical education");
        pragati.addRule(new EligibilityRule(RuleType.GENDER, Operator.EQUALS, "Female"));
        pragati.addRule(new EligibilityRule(RuleType.MIN_CGPA, Operator.GREATER_EQUAL, "6.5"));
        pragati.addRule(new EligibilityRule(RuleType.MAX_INCOME, Operator.LESS_EQUAL, "800000"));
        scholarshipDAO.insert(pragati);

        Scholarship obc = new Scholarship("OBC Pre-Matric Scholarship", 25000, "Ministry of Social Justice");
        obc.setDescription("For OBC category students from low-income families");
        obc.addRule(new EligibilityRule(RuleType.CATEGORY, Operator.IN_LIST, "OBC"));
        obc.addRule(new EligibilityRule(RuleType.MAX_INCOME, Operator.LESS_EQUAL, "200000"));
        obc.addRule(new EligibilityRule(RuleType.MIN_CGPA, Operator.GREATER_EQUAL, "6.0"));
        obc.addRule(new EligibilityRule(RuleType.MIN_ATTENDANCE, Operator.GREATER_EQUAL, "75"));
        scholarshipDAO.insert(obc);

        System.out.println("Sample data initialized successfully!");
    }
}
