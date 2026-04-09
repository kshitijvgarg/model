package com.scholarship.util;

import com.scholarship.dao.ScholarshipDAO;
import com.scholarship.dao.StudentDAO;
import com.scholarship.model.*;
import com.scholarship.model.EligibilityRule.Operator;
import com.scholarship.model.EligibilityRule.RuleType;

public class SampleDataInitializer {

    public static void initializeSampleData() throws Exception {
        StudentDAO studentDAO = new StudentDAO();
        ScholarshipDAO scholarshipDAO = new ScholarshipDAO();

        // Sample Students
        Student s1 = new Student("Rahul Sharma", "2024CS001", 8.5, 200000, "General", 92);
        s1.setCurrentSemester(4);
        s1.setGender("Male");
        studentDAO.insert(s1);

        Student s2 = new Student("Priya Patel", "2024CS002", 9.2, 150000, "OBC", 95);
        s2.setCurrentSemester(4);
        s2.setGender("Female");
        studentDAO.insert(s2);

        Student s3 = new Student("Amit Kumar", "2024CS003", 7.8, 350000, "SC", 88);
        s3.setCurrentSemester(4);
        s3.setGender("Male");
        s3.setBacklogCount(1);
        studentDAO.insert(s3);

        // Sample Scholarships
        Scholarship merit = new Scholarship("Merit Scholarship", 50000, "Institution");
        merit.setDescription("For students with excellent academic performance");
        merit.addRule(new EligibilityRule(RuleType.MIN_CGPA, Operator.GREATER_EQUAL, "8.5"));
        merit.addRule(new EligibilityRule(RuleType.MIN_ATTENDANCE, Operator.GREATER_EQUAL, "90"));
        merit.addRule(new EligibilityRule(RuleType.MAX_BACKLOGS, Operator.LESS_EQUAL, "0"));
        scholarshipDAO.insert(merit);

        Scholarship needBased = new Scholarship("Need-Based Scholarship", 40000, "Government");
        needBased.setDescription("For economically disadvantaged students");
        needBased.addRule(new EligibilityRule(RuleType.MAX_INCOME, Operator.LESS_EQUAL, "250000"));
        needBased.addRule(new EligibilityRule(RuleType.MIN_CGPA, Operator.GREATER_EQUAL, "6.5"));
        needBased.addRule(new EligibilityRule(RuleType.MIN_ATTENDANCE, Operator.GREATER_EQUAL, "75"));
        scholarshipDAO.insert(needBased);

        Scholarship scSt = new Scholarship("SC/ST Scholarship", 35000, "Government");
        scSt.setDescription("For SC/ST category students");
        scSt.addRule(new EligibilityRule(RuleType.CATEGORY, Operator.IN_LIST, "SC,ST"));
        scSt.addRule(new EligibilityRule(RuleType.MIN_CGPA, Operator.GREATER_EQUAL, "6.0"));
        scSt.addRule(new EligibilityRule(RuleType.MAX_INCOME, Operator.LESS_EQUAL, "300000"));
        scholarshipDAO.insert(scSt);

        System.out.println("Sample data initialized successfully!");
    }
}

