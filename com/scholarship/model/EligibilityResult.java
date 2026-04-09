package com.scholarship.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class EligibilityResult {
    private int id;
    private Student student;
    private Scholarship scholarship;
    private boolean isEligible;
    private List<String> passedRules;
    private List<String> failedRules;
    private LocalDateTime checkedAt;
    private String remarks;

    public EligibilityResult() {
        this.passedRules = new ArrayList<>();
        this.failedRules = new ArrayList<>();
        this.checkedAt = LocalDateTime.now();
    }

    public EligibilityResult(Student student, Scholarship scholarship) {
        this();
        this.student = student;
        this.scholarship = scholarship;
    }

    public void addPassedRule(String rule) {
        passedRules.add(rule);
    }

    public void addFailedRule(String rule) {
        failedRules.add(rule);
    }

    public void determineEligibility() {
        this.isEligible = failedRules.isEmpty();
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public Student getStudent() { return student; }
    public void setStudent(Student student) { this.student = student; }
    
    public Scholarship getScholarship() { return scholarship; }
    public void setScholarship(Scholarship scholarship) { this.scholarship = scholarship; }
    
    public boolean isEligible() { return isEligible; }
    public void setEligible(boolean eligible) { isEligible = eligible; }
    
    public List<String> getPassedRules() { return passedRules; }
    public List<String> getFailedRules() { return failedRules; }
    
    public LocalDateTime getCheckedAt() { return checkedAt; }
    public void setCheckedAt(LocalDateTime checkedAt) { this.checkedAt = checkedAt; }
    
    public String getRemarks() { return remarks; }
    public void setRemarks(String remarks) { this.remarks = remarks; }

    public String getSummary() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("Student: %s\n", student.getName()));
        sb.append(String.format("Scholarship: %s\n", scholarship.getName()));
        sb.append(String.format("Status: %s\n", isEligible ? "ELIGIBLE ✓" : "NOT ELIGIBLE ✗"));
        
        if (!passedRules.isEmpty()) {
            sb.append("\nPassed Criteria:\n");
            passedRules.forEach(r -> sb.append("  ✓ ").append(r).append("\n"));
        }
        
        if (!failedRules.isEmpty()) {
            sb.append("\nFailed Criteria:\n");
            failedRules.forEach(r -> sb.append("  ✗ ").append(r).append("\n"));
        }
        
        return sb.toString();
    }
}
