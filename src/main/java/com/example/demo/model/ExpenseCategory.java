package com.example.demo.model;

public enum ExpenseCategory {
    YEMEK("Yemek"),
    EGLENCE("Eğlence"),
    ULASIM("Ulaşım"),
    ALISVERIS("Alışveriş"),
    EGITIM("Eğitim"),
    SAGLIK("Sağlık"),
    DIGER("Diğer");

    private final String displayName;

    ExpenseCategory(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
} 