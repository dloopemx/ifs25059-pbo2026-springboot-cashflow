package org.delcom.ifs25059pbo2026springbootcashflow.usecase;

import org.delcom.ifs25059pbo2026springbootcashflow.domain.entity.SortOption;
import org.delcom.ifs25059pbo2026springbootcashflow.domain.entity.Transaction;
import org.delcom.ifs25059pbo2026springbootcashflow.domain.exception.EntityNotFoundException;
import org.delcom.ifs25059pbo2026springbootcashflow.domain.exception.ValidationException;
import org.delcom.ifs25059pbo2026springbootcashflow.domain.repository.ITransactionRepository;
import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

/** Aturan bisnis cashflow; tidak bergantung pada Vaadin, Spring UI, atau I/O. */
public class FinanceUseCase {
    private final ITransactionRepository repository;

    public FinanceUseCase(ITransactionRepository repository) { this.repository = repository; }

    public List<Transaction> getAllTransactions() { return repository.findAll(); }

    public Transaction addIncome(String description, BigDecimal amount) throws ValidationException {
        validate(description, amount);
        return repository.saveIncome(description.trim(), amount);
    }

    public Transaction addExpense(String description, BigDecimal amount) throws ValidationException {
        validate(description, amount);
        return repository.saveExpense(description.trim(), amount);
    }

    private void validate(String description, BigDecimal amount) throws ValidationException {
        if (description == null || description.trim().isEmpty())
            throw new ValidationException("Keterangan transaksi tidak boleh kosong.");
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0)
            throw new ValidationException("Jumlah harus berupa angka lebih besar dari nol.");
        if (amount.scale() > 2)
            throw new ValidationException("Jumlah maksimal memiliki dua angka di belakang koma.");
    }

    public List<Transaction> searchTransactions(String keyword) {
        if (keyword == null || keyword.isBlank()) return repository.findAll();
        String normalized = keyword.trim().toLowerCase(Locale.ROOT);
        return repository.findBy(t -> t.getDescription().toLowerCase(Locale.ROOT).contains(normalized));
    }

    public List<Transaction> sortTransactions(List<Transaction> transactions, SortOption option) {
        Comparator<Transaction> comparator = option == null
                ? SortOption.AMOUNT_ASC.comparator() : option.comparator();
        return transactions.stream().sorted(comparator.thenComparingInt(Transaction::getId)).toList();
    }

    public void removeTransaction(int id) throws EntityNotFoundException {
        if (id <= 0 || !repository.deleteById(id)) throw new EntityNotFoundException(id);
    }

    public BigDecimal getBalance() {
        return repository.findAll().stream().map(t -> t.getType().name().equals("INCOME")
                ? t.getAmount() : t.getAmount().negate()).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal getTotalIncome() {
        return repository.findBy(Transaction::isIncome).stream().map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal getTotalExpense() {
        return repository.findBy(t -> !t.isIncome()).stream().map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
