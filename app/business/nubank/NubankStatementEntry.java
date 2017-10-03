package business.nubank;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

import org.apache.poi.ss.usermodel.Row;

public class NubankStatementEntry {
	public static NubankStatementEntry NULL_STATEMENT_ENTRY = new NubankStatementEntry();

	public String date;

	public String bankDescription;

	public String value;

	public boolean expense;

	public Row rowData;

	public Row rowDescAndPrice;

	public NubankStatementEntry() {
	}

	public NubankStatementEntry(Row rowData, Row rowDescAndPrice) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM");
		NumberFormat numberFormat = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));

		this.rowData = rowData;
		this.rowDescAndPrice = rowDescAndPrice;

		this.date = dateFormat.format(rowData.getCell(0).getDateCellValue());
		this.bankDescription = rowDescAndPrice.getCell(0).getStringCellValue().trim();
		this.value = numberFormat.format(rowDescAndPrice.getCell(1).getNumericCellValue()).replace("R$", "").trim();
		this.expense = !this.value.contains("-");

		removeValueSignal();
	}

	private void removeValueSignal() {
		this.value = this.value.replaceAll("[+-]", "").trim();
	}

	public static NubankStatementEntry getStatementEntry(Row rowData, Row rowDescAndPrice) {
		if (rowData == null || rowDescAndPrice == null)
			return NULL_STATEMENT_ENTRY;

		return new NubankStatementEntry(rowData, rowDescAndPrice);
	}

	public Row getRowData() {
		return rowData;
	}

	public Row getRowDescAndPrice() {
		return rowDescAndPrice;
	}

	public boolean isExpense() {
		return expense;
	}

	@Override
	public String toString() {
		return date + ";" + bankDescription + ";" + value + ";" + expense;
	}
}
