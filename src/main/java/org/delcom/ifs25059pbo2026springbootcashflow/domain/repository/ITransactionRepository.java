package org.delcom.ifs25059pbo2026springbootcashflow.domain.repository;

import org.delcom.ifs25059pbo2026springbootcashflow.domain.entity.Transaction;
import java.math.BigDecimal;

public interface ITransactionRepository extends IRepository<Transaction, Integer> {
    Transaction saveIncome(String description, BigDecimal amount);
    Transaction saveExpense(String description, BigDecimal amount);
}
