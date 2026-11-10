package org.delcom.ifs25059pbo2026springbootcashflow.adapter.presenter;

import org.delcom.ifs25059pbo2026springbootcashflow.domain.entity.Transaction;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/** Mengubah data domain menjadi teks yang siap ditampilkan oleh UI. */
public class FinancePresenter {
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd MMM yyyy, HH:mm", Locale.forLanguageTag("id-ID"));

    public String formatMoney(BigDecimal amount) {
        DecimalFormatSymbols symbols = DecimalFormatSymbols.getInstance(Locale.US);
        DecimalFormat formatter = new DecimalFormat("#,##0.00", symbols);
        formatter.setRoundingMode(java.math.RoundingMode.HALF_UP);
        return "Rp " + formatter.format(amount == null ? BigDecimal.ZERO : amount);
    }

    public String formatTransaction(Transaction transaction) {
        return "[" + transaction.getId() + "] " + transaction.getDescription() + " | "
                + formatMoney(transaction.getAmount()) + " | [" + transaction.getType().getLabel() + "]";
    }

    public String formatBalance(BigDecimal balance) { return "Saldo: " + formatMoney(balance); }
    public String formatDate(Transaction transaction) { return transaction.getCreatedAt().format(DATE_FORMAT); }
}
