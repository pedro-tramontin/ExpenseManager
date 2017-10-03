package business.itau;

public class ItauStatementEntry {

	public static ItauStatementEntry NULL_STATEMENT_ENTRY = new ItauStatementEntry();
	
	public String date;

	public String bankDescription;

	public String value;

	public boolean expense;

	public ItauStatementEntry() {
	}

	private void removeValueSignal() {
		this.value = this.value.replaceAll("[+-]", "").trim();
	}

	public boolean isExpense() {
		return expense;
	}

	public void processExpense() {
		this.expense = this.value.contains("-");

		removeValueSignal();
	}

	@Override
	public String toString() {
		return date + ";" + bankDescription + ";" + value + ";" + expense;
	}
}