package business.nubank;

import java.util.Iterator;
import java.util.function.Consumer;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;

import play.Logger;
import business.excel.Excel;

public class NubankStatementEntryIterator implements Iterator<NubankStatementEntry> {

	private Excel excel;

	private Iterator<Row> excelIterator;

	private NubankStatementEntry currentStatement;

	private NubankStatementEntry nextStatement;

	public NubankStatementEntryIterator(Excel excel) {
		this.excel = excel;
		this.excelIterator = excel.iterator();
		this.nextStatement = getNextStatement();
	}

	@Override
	public boolean hasNext() {
		return !NubankStatementEntry.NULL_STATEMENT_ENTRY.equals(nextStatement);
	}

	@Override
	public NubankStatementEntry next() {
		currentStatement = nextStatement;

		nextStatement = getNextStatement();

		return currentStatement;
	}

	@Override
	public void remove() {
		excel.removeRow(currentStatement.getRowData());
		excel.removeRow(currentStatement.getRowDescAndPrice());
	}

	private NubankStatementEntry getNextStatement() {
		while (excelIterator.hasNext()) {
			Row rowData = excelIterator.next();
			Row rowDescAndPrice = excelIterator.next();

			if (rowHasExpenseData(rowData) && rowHasExpenseData(rowDescAndPrice)) {
				return NubankStatementEntry.getStatementEntry(rowData, rowDescAndPrice);
			}
		}

		return NubankStatementEntry.getStatementEntry(null, null);
	}

	private boolean rowHasExpenseData(Row row) {
		return rowIsNotNull(row) && rowHasOneOrTwoCells(row) && rowCellsAreNotEmpty(row) && rowIsNotSum(row);
	}

	private boolean rowIsNotNull(Row row) {
		boolean r = row != null;

		Logger.of(NubankStatementEntryIterator.class).debug(String.format("Row is not null: %s", r));

		return r;
	}

	private boolean rowHasOneOrTwoCells(Row row) {
		boolean r = row.getLastCellNum() == 1 || row.getLastCellNum() == 2;

		Logger.of(NubankStatementEntryIterator.class).debug(String.format("Row has one or two cells: %s", r));

		return r;
	}

	private boolean rowCellsAreNotEmpty(Row row) {
		Cell c1 = row.getCell(0, Row.RETURN_BLANK_AS_NULL);
		Cell c2 = row.getCell(1, Row.RETURN_BLANK_AS_NULL);

		boolean r = c1 != null || c2 != null;

		Logger.of(NubankStatementEntryIterator.class).debug(String.format("Row cells are not empty: %s", r));

		return r;
	}

	private boolean rowIsNotSum(Row row) {
		DataFormatter df = new DataFormatter();
		Cell c1 = row.getCell(0);
		
		boolean r = !StringUtils.containsIgnoreCase("/PAGAMENTO RECEBIDO/", df.formatCellValue(c1).trim());

		Logger.of(NubankStatementEntryIterator.class).debug(String.format("Row is not sum: %s", r));

		return r;
	}

	@Override
	public void forEachRemaining(Consumer<? super NubankStatementEntry> action) {
		// TODO Auto-generated method stub

	}
}
