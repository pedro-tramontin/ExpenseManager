package business.credicard;

import java.util.Iterator;
import java.util.function.Consumer;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Row;

import play.Logger;
import business.excel.Excel;

public class CredicardStatementEntryIterator implements
		Iterator<CredicardStatementEntry> {

	private Excel excel;

	private Iterator<Row> excelIterator;

	private CredicardStatementEntry currentStatement;

	private CredicardStatementEntry nextStatement;

	public CredicardStatementEntryIterator(Excel excel) {
		this.excel = excel;
		this.excelIterator = excel.iterator();
		this.nextStatement = getNextStatement();
	}

	@Override
	public boolean hasNext() {
		return !CredicardStatementEntry.NULL_STATEMENT_ENTRY
				.equals(nextStatement);
	}

	@Override
	public CredicardStatementEntry next() {
		currentStatement = nextStatement;

		nextStatement = getNextStatement();

		return currentStatement;
	}

	@Override
	public void remove() {
		excel.removeRow(currentStatement.getRow());
	}

	private CredicardStatementEntry getNextStatement() {
		while (excelIterator.hasNext()) {
			Row row = excelIterator.next();

			if (rowHasExpenseData(row)) {
				return CredicardStatementEntry.getStatementEntry(row);
			}
		}

		return CredicardStatementEntry.getStatementEntry(null);
	}

	private boolean rowHasExpenseData(Row row) {
		return rowIsNotNull(row) && rowHasThreeCells(row)
				&& rowCellsAreNotEmpty(row) && rowIsNotHeader(row)
				&& rowIsNotSum(row);
	}

	private boolean rowIsNotHeader(Row row) {
		boolean r = !StringUtils.equalsIgnoreCase("Data", row.getCell(0)
				.getStringCellValue())
				&& !StringUtils.equalsIgnoreCase("Description", row.getCell(1)
						.getStringCellValue())
				&& !StringUtils.equalsIgnoreCase("Value", row.getCell(2)
						.getStringCellValue());

		Logger.of(CredicardStatementEntryIterator.class).debug(
				String.format("Row is not header: %s", r));

		return r;
	}

	private boolean rowIsNotNull(Row row) {
		boolean r = row != null;

		Logger.of(CredicardStatementEntryIterator.class).debug(
				String.format("Row is not null: %s", r));

		return r;
	}

	private boolean rowHasThreeCells(Row row) {
		boolean r = row.getLastCellNum() == 3;

		Logger.of(CredicardStatementEntryIterator.class).debug(
				String.format("Row has three cells: %s", r));

		return r;
	}

	private boolean rowCellsAreNotEmpty(Row row) {
		boolean r = !StringUtils.isBlank(row.getCell(0).getStringCellValue())
				&& !StringUtils.isBlank(row.getCell(1).getStringCellValue())
				&& !StringUtils.isBlank(row.getCell(2).getStringCellValue());

		Logger.of(CredicardStatementEntryIterator.class).debug(
				String.format("Row cells are not empty: %s", r));

		return r;
	}

	private boolean rowIsNotSum(Row row) {
		boolean r = !StringUtils.containsIgnoreCase(
				"/TOTAL DA FATURA ANTERIOR/PAGAMENTO EFETUADO/", row.getCell(1)
						.getStringCellValue().trim());

		Logger.of(CredicardStatementEntryIterator.class).debug(
				String.format("Row is not sum: %s", r));

		return r;
	}

	@Override
	public void forEachRemaining(
			Consumer<? super CredicardStatementEntry> action) {
		// TODO Auto-generated method stub
		
	}
}
