package com.scholarship.model;

import java.util.ArrayList;
import java.util.List;

public class Scholarship {
    private int id;
    private String name;
    private String description;
    private double amount;
    private String provider;          // Government, Private, Institution
    private boolean isActive;
    private List<EligibilityRule> rules;

    public Scholarship() {
        this.rules = new ArrayList<>();
        this.isActive = true;
    }

    public Scholarship(String name, double amount, String provider) {
        this();
        this.name = name;
        this.amount = amount;
        this.provider = provider;
    }

    public void addRule(EligibilityRule rule) {
        rules.add(rule);
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }
    
    public String getProvider() { return provider; }
    public void setProvider(String provider) { this.provider = provider; }
    
    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }
    
    public List<EligibilityRule> getRules() { return rules; }
    public void setRules(List<EligibilityRule> rules) { this.rules = rules; }

    @Override
    public String toString() {
        return String.format("%s - ₹%.2f (%s)", name, amount, provider);
    }
}
