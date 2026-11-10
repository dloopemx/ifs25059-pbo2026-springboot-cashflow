package org.delcom.ifs25059pbo2026springbootcashflow.usecase;

import org.delcom.ifs25059pbo2026springbootcashflow.adapter.repository.TransactionRepository;
import org.delcom.ifs25059pbo2026springbootcashflow.domain.entity.SortOption;
import org.delcom.ifs25059pbo2026springbootcashflow.domain.exception.EntityNotFoundException;
import org.delcom.ifs25059pbo2026springbootcashflow.domain.exception.ValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class FinanceUseCaseTest {
    private FinanceUseCase finance;

    @BeforeEach void setUp() { finance = new FinanceUseCase(new TransactionRepository()); }

    @Test void incomeAndExpenseUpdateBalance() throws Exception {
        finance.addIncome("Gaji", new BigDecimal("5000000"));
        finance.addExpense("Makan", new BigDecimal("25000"));
        assertThat(finance.getBalance()).isEqualByComparingTo("4975000");
        assertThat(finance.getTotalIncome()).isEqualByComparingTo("5000000");
        assertThat(finance.getTotalExpense()).isEqualByComparingTo("25000");
    }

    @Test void rejectsBlankDescriptionAndNonPositiveAmount() {
        assertThatThrownBy(() -> finance.addIncome("  ", BigDecimal.ONE))
                .isInstanceOf(ValidationException.class);
        assertThatThrownBy(() -> finance.addExpense("Belanja", BigDecimal.ZERO))
                .isInstanceOf(ValidationException.class);
        assertThatThrownBy(() -> finance.addExpense("Belanja", new BigDecimal("-1")))
                .isInstanceOf(ValidationException.class);
    }

    @Test void searchesOnlyDescriptionAndSortsWithDomainOption() throws Exception {
        finance.addIncome("Gaji", new BigDecimal("500"));
        finance.addExpense("Makan", new BigDecimal("100"));
        assertThat(finance.searchTransactions("GAJ")).hasSize(1);
        assertThat(finance.sortTransactions(finance.getAllTransactions(), SortOption.AMOUNT_DESC)
                .get(0).getAmount()).isEqualByComparingTo("500");
    }

    @Test void deletingTransactionUpdatesBalanceAndMissingIdThrows() throws Exception {
        var transaction = finance.addIncome("Bonus", new BigDecimal("1000"));
        finance.removeTransaction(transaction.getId());
        assertThat(finance.getBalance()).isEqualByComparingTo("0");
        assertThatThrownBy(() -> finance.removeTransaction(999))
                .isInstanceOf(EntityNotFoundException.class);
    }
}
