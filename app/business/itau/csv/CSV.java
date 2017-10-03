package business.itau.csv;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Iterator;
import java.util.List;

import org.apache.poi.ss.usermodel.Row;

import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.bean.ColumnPositionMappingStrategy;
import au.com.bytecode.opencsv.bean.CsvToBean;
import business.itau.ItauStatementEntry;

public class CSV {
	private File csvFile;

	private List<ItauStatementEntry> listStatementEntry;

	public CSV(File csvFile) {
		this.csvFile = csvFile;
	}

	public void open() {
		try {
			CsvToBean<ItauStatementEntry> csvToBean = new CsvToBean<ItauStatementEntry>();
			CSVReader csvReader = new CSVReader(new FileReader(csvFile), ';');

			String[] columns = new String[] { "date", "bankDescription", "value" };

			ColumnPositionMappingStrategy<ItauStatementEntry> strategy = new ColumnPositionMappingStrategy<ItauStatementEntry>();
			strategy.setType(ItauStatementEntry.class);
			strategy.setColumnMapping(columns);

			listStatementEntry = csvToBean.parse(strategy, csvReader);
		} catch (FileNotFoundException e) {
			throw new RuntimeException("Arquivo não encontrado: " + e.getMessage());
		}
	}

	public boolean hasHeader() {
		return false;
	}

	public boolean isOpen() {
		if (listStatementEntry == null) {
			return false;
		}

		return true;
	}

	public void removeRow(Row row) {
		// TODO
	}

	public Iterator<ItauStatementEntry> iterator() {
		return listStatementEntry.iterator();
	}
}
