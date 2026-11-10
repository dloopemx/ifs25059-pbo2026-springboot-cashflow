package org.delcom.ifs25059pbo2026springbootcashflow;

import org.delcom.ifs25059pbo2026springbootcashflow.adapter.presenter.FinancePresenter;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import static org.assertj.core.api.Assertions.assertThat;

class FinancePresenterTest {
    @Test void formatsMoneyUsingRequiredGroupingAndDecimals() {
        assertThat(new FinancePresenter().formatMoney(new BigDecimal("1000000")))
                .isEqualTo("Rp 1,000,000.00");
    }
}
