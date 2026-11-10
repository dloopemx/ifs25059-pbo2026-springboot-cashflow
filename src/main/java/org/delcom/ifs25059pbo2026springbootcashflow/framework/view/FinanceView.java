package org.delcom.ifs25059pbo2026springbootcashflow.framework.view;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.delcom.ifs25059pbo2026springbootcashflow.adapter.presenter.FinancePresenter;
import org.delcom.ifs25059pbo2026springbootcashflow.domain.entity.SortOption;
import org.delcom.ifs25059pbo2026springbootcashflow.domain.entity.Transaction;
import org.delcom.ifs25059pbo2026springbootcashflow.domain.exception.EntityNotFoundException;
import org.delcom.ifs25059pbo2026springbootcashflow.domain.exception.ValidationException;
import org.delcom.ifs25059pbo2026springbootcashflow.usecase.FinanceUseCase;

import java.math.BigDecimal;
import java.util.List;

@Route("")
@PageTitle("Cashflow | Catatan Keuangan")
public class FinanceView extends VerticalLayout {
    private final FinanceUseCase finance;
    private final FinancePresenter presenter;
    private final Grid<Transaction> grid = new Grid<>(Transaction.class, false);
    private final TextField searchField = new TextField();
    private final ComboBox<SortOption> sortBox = new ComboBox<>();
    private final Span totalTransactions = new Span("0");
    private final Span totalIncome = new Span("Rp 0.00");
    private final Span totalExpense = new Span("Rp 0.00");
    private final Span balance = new Span("Rp 0.00");

    public FinanceView(FinanceUseCase finance, FinancePresenter presenter) {
        this.finance = finance;
        this.presenter = presenter;
        setSizeFull();
        setPadding(true);
        setSpacing(true);
        getStyle().set("max-width", "1250px").set("margin", "0 auto");

        H1 title = new H1("Catatan Keuangan");
        title.getStyle().set("margin", "0").set("color", "#173b67");
        Span subtitle = new Span("Kelola pemasukan, pengeluaran, dan pantau saldo keuanganmu.");
        subtitle.getStyle().set("color", "#64748b");

        configureGrid();
        add(title, subtitle, buildSummary(), buildToolbar(), grid);
        refreshGrid();
    }

    private HorizontalLayout buildSummary() {
        HorizontalLayout cards = new HorizontalLayout(
                summaryCard("Total Transaksi", totalTransactions, "#eff6ff", "#1d4ed8"),
                summaryCard("Total Pemasukan", totalIncome, "#ecfdf5", "#047857"),
                summaryCard("Total Pengeluaran", totalExpense, "#fff7ed", "#c2410c"),
                summaryCard("Saldo Saat Ini", balance, "#f5f3ff", "#6d28d9"));
        cards.setWidthFull();
        cards.setSpacing(true);
        cards.getStyle().set("flex-wrap", "wrap");
        return cards;
    }

    private Div summaryCard(String label, Span value, String background, String accent) {
        Span caption = new Span(label);
        caption.getStyle().set("font-size", "0.85rem").set("font-weight", "600").set("color", accent);
        value.getStyle().set("display", "block").set("font-size", "1.45rem")
                .set("font-weight", "700").set("margin-top", "8px").set("color", "#172554");
        Div card = new Div(caption, value);
        card.getStyle().set("flex", "1 1 190px").set("min-width", "170px")
                .set("padding", "18px").set("border-radius", "12px")
                .set("background", background).set("border", "1px solid #e2e8f0");
        return card;
    }

    private HorizontalLayout buildToolbar() {
        searchField.setPlaceholder("Cari keterangan transaksi...");
        searchField.setClearButtonVisible(true);
        searchField.setWidth("min(100%, 330px)");
        searchField.setValueChangeMode(ValueChangeMode.EAGER);
        searchField.addValueChangeListener(event -> refreshGrid());

        sortBox.setItems(SortOption.values());
        sortBox.setItemLabelGenerator(SortOption::getLabel);
        sortBox.setPlaceholder("Urutkan transaksi");
        sortBox.setClearButtonVisible(true);
        sortBox.setWidth("220px");
        sortBox.addValueChangeListener(event -> refreshGrid());

        Button refresh = new Button("Muat Ulang", event -> refreshGrid());
        Button income = new Button("+ Pemasukan", event -> openTransactionDialog(true));
        income.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_SUCCESS);
        Button expense = new Button("− Pengeluaran", event -> openTransactionDialog(false));
        expense.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_ERROR);

        HorizontalLayout toolbar = new HorizontalLayout(searchField, sortBox, refresh, income, expense);
        toolbar.setWidthFull();
        toolbar.setAlignItems(Alignment.END);
        toolbar.getStyle().set("flex-wrap", "wrap");
        return toolbar;
    }

    private void configureGrid() {
        grid.addColumn(Transaction::getId).setHeader("ID").setAutoWidth(true).setFlexGrow(0).setSortable(true);
        grid.addColumn(Transaction::getDescription).setHeader("Keterangan").setFlexGrow(1).setSortable(true);
        grid.addColumn(t -> presenter.formatMoney(t.getAmount())).setHeader("Jumlah").setAutoWidth(true).setSortable(true);
        grid.addColumn(t -> t.getType().getLabel()).setHeader("Tipe").setAutoWidth(true).setSortable(true);
        grid.addColumn(presenter::formatDate).setHeader("Waktu").setAutoWidth(true).setSortable(true);
        grid.addComponentColumn(transaction -> {
            Button delete = new Button("Hapus", event -> confirmDelete(transaction));
            delete.addThemeVariants(ButtonVariant.LUMO_ERROR, ButtonVariant.LUMO_SMALL);
            return delete;
        }).setHeader("Tindakan").setAutoWidth(true).setFlexGrow(0);
        grid.setWidthFull();
        grid.setEmptyStateText("Belum ada transaksi. Tambahkan pemasukan atau pengeluaran pertama.");
    }

    private void refreshGrid() {
        try {
            List<Transaction> rows = finance.searchTransactions(searchField.getValue());
            SortOption selected = sortBox.getValue();
            if (selected != null) rows = finance.sortTransactions(rows, selected);
            grid.setItems(rows);
            totalTransactions.setText(String.valueOf(finance.getAllTransactions().size()));
            totalIncome.setText(presenter.formatMoney(finance.getTotalIncome()));
            totalExpense.setText(presenter.formatMoney(finance.getTotalExpense()));
            balance.setText(presenter.formatMoney(finance.getBalance()));
        } catch (RuntimeException exception) {
            showError("Gagal memuat transaksi: " + exception.getMessage());
        }
    }

    private void openTransactionDialog(boolean isIncome) {
        Dialog dialog = new Dialog();
        dialog.setHeaderTitle(isIncome ? "Tambah Pemasukan" : "Tambah Pengeluaran");
        dialog.setWidth("min(460px, 95vw)");

        TextField description = new TextField("Keterangan");
        description.setPlaceholder("Contoh: Gaji bulanan");
        description.setWidthFull();
        description.setRequired(true);
        TextField amount = new TextField("Jumlah (Rp)");
        amount.setPlaceholder("Contoh: 1500000 atau 1500000.50");
        amount.setWidthFull();
        amount.setRequired(true);
        amount.setHelperText("Gunakan angka positif; maksimal dua angka desimal.");

        Button save = new Button("Simpan", event -> {
            try {
                BigDecimal value = new BigDecimal(amount.getValue().trim().replace(",", ""));
                if (isIncome) finance.addIncome(description.getValue(), value);
                else finance.addExpense(description.getValue(), value);
                dialog.close();
                refreshGrid();
                showSuccess(isIncome ? "Pemasukan berhasil ditambahkan." : "Pengeluaran berhasil ditambahkan.");
            } catch (NumberFormatException exception) {
                showError("Jumlah harus berupa angka yang valid.");
            } catch (ValidationException exception) {
                showError(exception.getMessage());
            }
        });
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        Button cancel = new Button("Batal", event -> dialog.close());
        HorizontalLayout actions = new HorizontalLayout(cancel, save);
        actions.setWidthFull();
        actions.setJustifyContentMode(JustifyContentMode.END);
        VerticalLayout content = new VerticalLayout(description, amount, actions);
        content.setPadding(false);
        dialog.add(content);
        dialog.open();
    }

    private void confirmDelete(Transaction transaction) {
        Dialog dialog = new Dialog();
        dialog.setHeaderTitle("Konfirmasi Hapus");
        dialog.add(new Span("Hapus transaksi '" + transaction.getDescription() + "'?"));
        Button cancel = new Button("Batal", event -> dialog.close());
        Button delete = new Button("Ya, Hapus", event -> {
            try {
                finance.removeTransaction(transaction.getId());
                dialog.close();
                refreshGrid();
                showSuccess("Transaksi berhasil dihapus.");
            } catch (EntityNotFoundException exception) {
                dialog.close();
                showError(exception.getMessage());
            }
        });
        delete.addThemeVariants(ButtonVariant.LUMO_ERROR, ButtonVariant.LUMO_PRIMARY);
        HorizontalLayout actions = new HorizontalLayout(cancel, delete);
        actions.setWidthFull();
        actions.setJustifyContentMode(JustifyContentMode.END);
        dialog.getFooter().add(actions);
        dialog.open();
    }

    private void showSuccess(String message) {
        Notification notification = Notification.show(message, 3000, Notification.Position.TOP_END);
        notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
    }

    private void showError(String message) {
        Notification notification = Notification.show(message, 4500, Notification.Position.TOP_END);
        notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
    }
}
