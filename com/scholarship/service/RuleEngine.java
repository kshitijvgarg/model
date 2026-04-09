package com.scholarship.service;

import com.scholarship.model.EligibilityRule;
import com.scholarship.model.EligibilityRule.RuleType;
import com.scholarship.model.Student;

import java.util.Arrays;
import java.util.List;

public class RuleEngine {

    /**
     * Evaluates a single rule against a student's data.
     * @return true if the student satisfies the rule
     */
    public boolean evaluate(Student student, EligibilityRule rule) {
        double studentValue = getStudentValue(student, rule.getRuleType());
        String studentStringValue = getStudentStringValue(student, rule.getRuleType());
        
        return switch (rule.getOperator()) {
            case GREATER_THAN -> studentValue > parseDouble(rule.getValue());
            case LESS_THAN -> studentValue < parseDouble(rule.getValue());
            case GREATER_EQUAL -> studentValue >= parseDouble(rule.getValue());
            case LESS_EQUAL -> studentValue <= parseDouble(rule.getValue());
            case EQUALS -> studentStringValue.equalsIgnoreCase(rule.getValue());
            case NOT_EQUALS -> !studentStringValue.equalsIgnoreCase(rule.getValue());
            case IN_LIST -> isInList(studentStringValue, rule.getValue());
        };
    }

    /**
     * Gets the numeric value from student based on rule type.
     */
    private double getStudentValue(Student student, RuleType ruleType) {
        return switch (ruleType) {
            case MIN_CGPA -> student.getCgpa();
            case MAX_INCOME -> student.getFamilyIncome();
            case MIN_ATTENDANCE -> student.getAttendancePercent();
            case MAX_BACKLOGS -> student.getBacklogCount();
            case MIN_SEMESTER -> student.getCurrentSemester();
            default -> 0.0;
        };
    }

    /**
     * Gets the string value from student based on rule type.
     */
    private String getStudentStringValue(Student student, RuleType ruleType) {
        return switch (ruleType) {
            case CATEGORY -> student.getCategory() != null ? student.getCategory() : "";
            case GENDER -> student.getGender() != null ? student.getGender() : "";
            case DISABILITY -> student.isDisabled() ? "YES" : "NO";
            case STATE -> student.getState() != null ? student.getState() : "";
            default -> String.valueOf(getStudentValue(student, ruleType));
        };
    }

    /**
     * Checks if a value exists in a comma-separated list.
     */
    private boolean isInList(String value, String listValue) {
        List<String> list = Arrays.asList(listValue.split(","));
        return list.stream()
                   .map(String::trim)
                   .map(String::toUpperCase)
                   .anyMatch(item -> item.equals(value.toUpperCase()));
    }

    private double parseDouble(String value) {
        try {
            return Double.parseDouble(value.trim());
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }

    /**
     * Generates a human-readable description of the rule evaluation.
     */
    public String getRuleDescription(EligibilityRule rule, boolean passed) {
        String criterion = switch (rule.getRuleType()) {
            case MIN_CGPA -> String.format("CGPA ≥ %s", rule.getValue());
            case MAX_INCOME -> String.format("Family Income ≤ ₹%s", rule.getValue());
            case MIN_ATTENDANCE -> String.format("Attendance ≥ %s%%", rule.getValue());
            case CATEGORY -> String.format("Category in [%s]", rule.getValue());
            case MAX_BACKLOGS -> String.format("Backlogs ≤ %s", rule.getValue());
            case GENDER -> String.format("Gender = %s", rule.getValue());
            case DISABILITY -> String.format("Disability status = %s", rule.getValue());
            case STATE -> String.format("State in [%s]", rule.getValue());
            case MIN_SEMESTER -> String.format("Semester ≥ %s", rule.getValue());
        };
        
        return criterion;
    }
}
