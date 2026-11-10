package org.delcom.ifs25059pbo2026springbootcashflow.domain.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

/** Entitas transaksi; identitas tidak dapat diubah setelah transaksi dibuat. */
public class Transaction {
    private final int id;
    private final String description;
    private final BigDecimal amount;
    private final TransactionType type;
    private final LocalDateTime createdAt;

    public Transaction(int id, String description, BigDecimal amount, TransactionType type) {
        this(id, description, amount, type, LocalDateTime.now());
    }

    public Transaction(int id, String description, BigDecimal amount, TransactionType type,
                       LocalDateTime createdAt) {
        this.id = id;
        this.description = Objects.requireNonNull(description);
        this.amount = Objects.requireNonNull(amount);
        this.type = Objects.requireNonNull(type);
        this.createdAt = Objects.requireNonNull(createdAt);
    }

    public int getId() { return id; }
    public String getDescription() { return description; }
    public BigDecimal getAmount() { return amount; }
    public TransactionType getType() { return type; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public boolean isIncome() { return type == TransactionType.INCOME; }
}
