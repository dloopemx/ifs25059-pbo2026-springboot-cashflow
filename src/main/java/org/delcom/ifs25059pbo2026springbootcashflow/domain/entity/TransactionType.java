package org.delcom.ifs25059pbo2026springbootcashflow.domain.entity;

public enum TransactionType {
    INCOME("Pemasukan"), EXPENSE("Pengeluaran");

    private final String label;
    TransactionType(String label) { this.label = label; }
    public String getLabel() { return label; }
}
