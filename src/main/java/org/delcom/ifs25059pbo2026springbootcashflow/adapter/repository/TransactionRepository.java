package org.delcom.ifs25059pbo2026springbootcashflow.adapter.repository;

import org.delcom.ifs25059pbo2026springbootcashflow.domain.entity.Transaction;
import org.delcom.ifs25059pbo2026springbootcashflow.domain.entity.TransactionType;
import org.delcom.ifs25059pbo2026springbootcashflow.domain.repository.ITransactionRepository;
import java.math.BigDecimal;
import java.util.concurrent.atomic.AtomicInteger;

public class TransactionRepository extends InMemoryRepository<Transaction, Integer>
        implements ITransactionRepository {
    private final AtomicInteger sequence = new AtomicInteger(0);

    public TransactionRepository() { super(Transaction::getId); }

    @Override public synchronized Transaction save(Transaction transaction) {
        sequence.accumulateAndGet(transaction.getId(), Math::max);
        return super.save(transaction);
    }

    @Override public synchronized Transaction saveIncome(String description, BigDecimal amount) {
        Transaction transaction = new Transaction(sequence.incrementAndGet(), description, amount,
                TransactionType.INCOME);
        return super.save(transaction);
    }

    @Override public synchronized Transaction saveExpense(String description, BigDecimal amount) {
        Transaction transaction = new Transaction(sequence.incrementAndGet(), description, amount,
                TransactionType.EXPENSE);
        return super.save(transaction);
    }
}
