package com.example.demo.dto;

import java.math.BigDecimal;
import java.util.List;

import com.example.demo.model.ExpenseCategory;

import lombok.Data;

@Data
public class ExpenseCreateDTO {
    private List<Long> groupIds;
    private Long userId;
    private String description;
    private BigDecimal amount;
    private ExpenseCategory category;
    private boolean split;
    private List<Long> splitMembers;

    public List<Long> getGroupIds() {
        return groupIds;
    }

    public void setGroupIds(List<Long> groupIds) {
        this.groupIds = groupIds;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public ExpenseCategory getCategory() {
        return category;
    }

    public void setCategory(ExpenseCategory category) {
        this.category = category;
    }

    public boolean isSplit() {
        return split;
    }

    public void setSplit(boolean split) {
        this.split = split;
    }
}

   
 