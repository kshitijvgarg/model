package com.scholarship.model;

import java.time.LocalDate;

public class Student {
    private int id;
    private String name;
    private String rollNumber;
    private String email;
    private double cgpa;              // 0.0 - 10.0
    private double familyIncome;      // Annual income
    private String category;          // General, OBC, SC, ST, EWS
    private double attendancePercent; // 0 - 100
    private int backlogCount;
    private LocalDate dateOfBirth;
    private String gender;
    private boolean isDisabled;
    private String state;
    private int currentSemester;

    // Constructor
    public Student() {}

    public Student(String name, String rollNumber, double cgpa, 
                   double familyIncome, String category, double attendancePercent) {
        this.name = name;
        this.rollNumber = rollNumber;
        this.cgpa = cgpa;
        this.familyIncome = familyIncome;
        this.category = category;
        this.attendancePercent = attendancePercent;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getRollNumber() { return rollNumber; }
    public void setRollNumber(String rollNumber) { this.rollNumber = rollNumber; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public double getCgpa() { return cgpa; }
    public void setCgpa(double cgpa) { this.cgpa = cgpa; }
    
    public double getFamilyIncome() { return familyIncome; }
    public void setFamilyIncome(double familyIncome) { this.familyIncome = familyIncome; }
    
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    
    public double getAttendancePercent() { return attendancePercent; }
    public void setAttendancePercent(double attendancePercent) { 
        this.attendancePercent = attendancePercent; 
    }
    
    public int getBacklogCount() { return backlogCount; }
    public void setBacklogCount(int backlogCount) { this.backlogCount = backlogCount; }
    
    public LocalDate getDateOfBirth() { return dateOfBirth; }
    public void setDateOfBirth(LocalDate dateOfBirth) { this.dateOfBirth = dateOfBirth; }
    
    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }
    
    public boolean isDisabled() { return isDisabled; }
    public void setDisabled(boolean disabled) { isDisabled = disabled; }
    
    public String getState() { return state; }
    public void setState(String state) { this.state = state; }
    
    public int getCurrentSemester() { return currentSemester; }
    public void setCurrentSemester(int currentSemester) { this.currentSemester = currentSemester; }

    @Override
    public String toString() {
        return String.format("%s (%s) - CGPA: %.2f", name, rollNumber, cgpa);
    }
    
}
