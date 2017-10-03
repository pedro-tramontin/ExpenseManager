package business.itau;

import java.util.Iterator;
import java.util.function.Consumer;

import business.itau.csv.CSV;

public class ItauStatementEntryIterator implements Iterator<ItauStatementEntry> {

	private Iterator<ItauStatementEntry> csvIterator;

	private ItauStatementEntry currentStatement;

	private ItauStatementEntry nextStatement;

	public ItauStatementEntryIterator(CSV csv) {
		this.csvIterator = csv.iterator();
		this.nextStatement = getNextStatement();
	}

	@Override
	public boolean hasNext() {
		return !ItauStatementEntry.NULL_STATEMENT_ENTRY.equals(nextStatement);
	}

	@Override
	public ItauStatementEntry next() {
		currentStatement = nextStatement;

		nextStatement = getNextStatement();

		return currentStatement;
	}

	@Override
	public void remove() {
		throw new RuntimeException("Not implementd");
	}

	private ItauStatementEntry getNextStatement() {
		if (csvIterator.hasNext()) {
			ItauStatementEntry entry = csvIterator.next();
			entry.processExpense();

			return entry;
		}

		return ItauStatementEntry.NULL_STATEMENT_ENTRY;
	}

	@Override
	public void forEachRemaining(Consumer<? super ItauStatementEntry> action) {
		// TODO Auto-generated method stub

	}
}