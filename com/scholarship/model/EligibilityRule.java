package com.scholarship.model;

public class EligibilityRule {
    
    public enum RuleType {
        MIN_CGPA,
        MAX_INCOME,
        MIN_ATTENDANCE,
        CATEGORY,
        MAX_BACKLOGS,
        GENDER,
        DISABILITY,
        STATE,
        MIN_SEMESTER
    }

    public enum Operator {
        GREATER_THAN,
        LESS_THAN,
        GREATER_EQUAL,
        LESS_EQUAL,
        EQUALS,
        NOT_EQUALS,
        IN_LIST
    }

    private int id;
    private int scholarshipId;
    private RuleType ruleType;
    private Operator operator;
    private String value;           // Can be number or comma-separated list
    private String description;
    private boolean isMandatory;

    public EligibilityRule() {
        this.isMandatory = true;
    }

    public EligibilityRule(RuleType ruleType, Operator operator, String value) {
        this();
        this.ruleType = ruleType;
        this.operator = operator;
        this.value = value;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public int getScholarshipId() { return scholarshipId; }
    public void setScholarshipId(int scholarshipId) { this.scholarshipId = scholarshipId; }
    
    public RuleType getRuleType() { return ruleType; }
    public void setRuleType(RuleType ruleType) { this.ruleType = ruleType; }
    
    public Operator getOperator() { return operator; }
    public void setOperator(Operator operator) { this.operator = operator; }
    
    public String getValue() { return value; }
    public void setValue(String value) { this.value = value; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public boolean isMandatory() { return isMandatory; }
    public void setMandatory(boolean mandatory) { isMandatory = mandatory; }

    @Override
    public String toString() {
        return String.format("%s %s %s", ruleType, operator, value);
    }
}
