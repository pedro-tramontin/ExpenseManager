package business.credicard;

import org.apache.poi.ss.usermodel.Row;

public class CredicardStatementEntry {
	public static CredicardStatementEntry NULL_STATEMENT_ENTRY = new CredicardStatementEntry();

	public String date;

	public String bankDescription;

	public String value;

	public boolean expense;

	public Row row;

	public CredicardStatementEntry() {
	}

	public CredicardStatementEntry(Row row) {
		this.row = row;

		this.date = row.getCell(0).getStringCellValue().trim();
		this.bankDescription = row.getCell(1).getStringCellValue().trim();
		this.value = row.getCell(2).getStringCellValue();
		this.expense = this.value.contains("-");

		removeValueSignal();
	}

	private void removeValueSignal() {
		this.value = this.value.replaceAll("[+-]", "").trim();
	}

	public static CredicardStatementEntry getStatementEntry(Row row) {
		if (row == null)
			return NULL_STATEMENT_ENTRY;

		return new CredicardStatementEntry(row);
	}

	public Row getRow() {
		return row;
	}

	public boolean isExpense() {
		return expense;
	}

	@Override
	public String toString() {
		return date + ";" + bankDescription + ";" + value + ";" + expense;
	}
}
