package com.scholarship.service;

import com.scholarship.dao.ScholarshipDAO;
import com.scholarship.dao.StudentDAO;
import com.scholarship.model.*;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class EligibilityService {
    
    private final RuleEngine ruleEngine;
    private final StudentDAO studentDAO;
    private final ScholarshipDAO scholarshipDAO;

    public EligibilityService() {
        this.ruleEngine = new RuleEngine();
        this.studentDAO = new StudentDAO();
        this.scholarshipDAO = new ScholarshipDAO();
    }

    /**
     * Check eligibility of a single student for a single scholarship.
     */
    public EligibilityResult checkEligibility(Student student, Scholarship scholarship) {
        EligibilityResult result = new EligibilityResult(student, scholarship);

        for (EligibilityRule rule : scholarship.getRules()) {
            boolean passed = ruleEngine.evaluate(student, rule);
            String description = ruleEngine.getRuleDescription(rule, passed);

            if (passed) {
                result.addPassedRule(description);
            } else {
                result.addFailedRule(description);
            }
        }

        result.determineEligibility();
        return result;
    }

    /**
     * Check all active scholarships for a student.
     */
    public List<EligibilityResult> checkAllScholarships(Student student) throws SQLException {
        List<EligibilityResult> results = new ArrayList<>();
        List<Scholarship> scholarships = scholarshipDAO.findAllActive();

        for (Scholarship scholarship : scholarships) {
            results.add(checkEligibility(student, scholarship));
        }

        return results;
    }

    /**
     * Check all students for a specific scholarship.
     */
    public List<EligibilityResult> checkAllStudents(Scholarship scholarship) throws SQLException {
        List<EligibilityResult> results = new ArrayList<>();
        List<Student> students = studentDAO.findAllActive();

        for (Student student : students) {
            results.add(checkEligibility(student, scholarship));
        }

        return results;
    }

    /**
     * Get only eligible results from a list.
     */
    public List<EligibilityResult> filterEligible(List<EligibilityResult> results) {
        return results.stream()
                      .filter(EligibilityResult::isEligible)
                      .toList();
    }

    /**
     * Get statistics for a scholarship check.
     */
    public EligibilityStats getStats(List<EligibilityResult> results) {
        long eligible = results.stream().filter(EligibilityResult::isEligible).count();
        long total = results.size();
        
        return new EligibilityStats(total, eligible, total - eligible);
    }

    public record EligibilityStats(long total, long eligible, long notEligible) {
        public double eligibilityRate() {
            return total > 0 ? (eligible * 100.0 / total) : 0;
        }
    }
}
