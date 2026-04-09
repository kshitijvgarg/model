package com.scholarship.dao;

import com.scholarship.model.EligibilityRule;
import com.scholarship.model.Scholarship;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ScholarshipDAO {

    public int insert(Scholarship scholarship) throws SQLException {
        String sql = """
            INSERT INTO scholarships (name, description, amount, provider, is_active)
            VALUES (?, ?, ?, ?, ?)
            """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setString(1, scholarship.getName());
            pstmt.setString(2, scholarship.getDescription());
            pstmt.setDouble(3, scholarship.getAmount());
            pstmt.setString(4, scholarship.getProvider());
            pstmt.setInt(5, scholarship.isActive() ? 1 : 0);

            pstmt.executeUpdate();

            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                if (rs.next()) {
                    int id = rs.getInt(1);
                    scholarship.setId(id);
                    
                    // Insert associated rules
                    for (EligibilityRule rule : scholarship.getRules()) {
                        rule.setScholarshipId(id);
                        insertRule(rule);
                    }
                    return id;
                }
            }
        }
        return -1;
    }

    public void insertRule(EligibilityRule rule) throws SQLException {
        String sql = """
            INSERT INTO eligibility_rules (scholarship_id, rule_type, operator, value, 
                description, is_mandatory)
            VALUES (?, ?, ?, ?, ?, ?)
            """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, rule.getScholarshipId());
            pstmt.setString(2, rule.getRuleType().name());
            pstmt.setString(3, rule.getOperator().name());
            pstmt.setString(4, rule.getValue());
            pstmt.setString(5, rule.getDescription());
            pstmt.setInt(6, rule.isMandatory() ? 1 : 0);

            pstmt.executeUpdate();
        }
    }

    public Optional<Scholarship> findById(int id) throws SQLException {
        String sql = "SELECT * FROM scholarships WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Scholarship scholarship = mapResultSetToScholarship(rs);
                    scholarship.setRules(findRulesByScholarshipId(id));
                    return Optional.of(scholarship);
                }
            }
        }
        return Optional.empty();
    }

    public List<Scholarship> findAllActive() throws SQLException {

    List<Scholarship> list = new ArrayList<>();
    String sql = "SELECT * FROM scholarships";

    try (Connection conn = DatabaseConnection.getConnection();
         Statement stmt = conn.createStatement();
         ResultSet rs = stmt.executeQuery(sql)) {

        while (rs.next()) {

            Scholarship s = new Scholarship(
                rs.getString("name"),
                rs.getDouble("amount"),
                rs.getString("provider")
            );

            s.setId(rs.getInt("id"));
            s.setDescription(rs.getString("description"));

            list.add(s);
        }
    }

    return list;
}

    public List<EligibilityRule> findRulesByScholarshipId(int scholarshipId) throws SQLException {
        List<EligibilityRule> rules = new ArrayList<>();
        String sql = "SELECT * FROM eligibility_rules WHERE scholarship_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, scholarshipId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    rules.add(mapResultSetToRule(rs));
                }
            }
        }
        return rules;
    }

    private Scholarship mapResultSetToScholarship(ResultSet rs) throws SQLException {
        Scholarship scholarship = new Scholarship();
        scholarship.setId(rs.getInt("id"));
        scholarship.setName(rs.getString("name"));
        scholarship.setDescription(rs.getString("description"));
        scholarship.setAmount(rs.getDouble("amount"));
        scholarship.setProvider(rs.getString("provider"));
        scholarship.setActive(rs.getInt("is_active") == 1);
        return scholarship;
    }

    private EligibilityRule mapResultSetToRule(ResultSet rs) throws SQLException {
        EligibilityRule rule = new EligibilityRule();
        rule.setId(rs.getInt("id"));
        rule.setScholarshipId(rs.getInt("scholarship_id"));
        rule.setRuleType(EligibilityRule.RuleType.valueOf(rs.getString("rule_type")));
        rule.setOperator(EligibilityRule.Operator.valueOf(rs.getString("operator")));
        rule.setValue(rs.getString("value"));
        rule.setDescription(rs.getString("description"));
        rule.setMandatory(rs.getInt("is_mandatory") == 1);
        return rule;
    }
}
