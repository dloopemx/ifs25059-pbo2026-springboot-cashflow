package org.delcom.ifs25059pbo2026springbootcashflow;

import org.delcom.ifs25059pbo2026springbootcashflow.adapter.presenter.FinancePresenter;
import org.delcom.ifs25059pbo2026springbootcashflow.domain.repository.ITransactionRepository;
import org.delcom.ifs25059pbo2026springbootcashflow.usecase.FinanceUseCase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class AppTest {
    @Autowired private ITransactionRepository transactionRepository;
    @Autowired private FinanceUseCase financeUseCase;
    @Autowired private FinancePresenter financePresenter;

    @Test
    void contextLoadsAndCleanArchitectureBeansAreWired() {
        assertThat(transactionRepository).isNotNull();
        assertThat(financeUseCase).isNotNull();
        assertThat(financePresenter).isNotNull();
    }
}
