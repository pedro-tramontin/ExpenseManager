package business.excel;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Iterator;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class Excel {
	private File excelFile;

	private String originalFilename;

	private HSSFWorkbook hssfWorkbook;

	private XSSFWorkbook xssfWorkbook;

	public Excel(File excelFile) {
		this.excelFile = excelFile;
	}

	public void openWorkbook() {
		try {
			if (originalFilename.endsWith(".xls")) {
				this.hssfWorkbook = new HSSFWorkbook(new FileInputStream(excelFile));
			} else if (originalFilename.endsWith(".xlsx")) {
				this.xssfWorkbook = new XSSFWorkbook(new FileInputStream(excelFile));
			}
		} catch (FileNotFoundException e) {
			throw new RuntimeException("Arquivo não encontrado: " + e.getMessage());
		} catch (IOException e) {
			throw new RuntimeException("Erro de leitura no arquivo: " + e.getMessage());
		}
	}

	public boolean hasHeader() {
		if (hssfWorkbook != null) {
			return StringUtils.equalsIgnoreCase("Data",
					hssfWorkbook.getSheetAt(0).getRow(0).getCell(0).getStringCellValue().trim());
		} else {
			return StringUtils.equalsIgnoreCase("Data",
					xssfWorkbook.getSheetAt(0).getRow(0).getCell(0).getStringCellValue().trim());
		}
	}

	public boolean isOpen() {
		if (hssfWorkbook == null && xssfWorkbook == null) {
			return false;
		}

		return true;
	}

	public void removeRow(Row row) {
		if (hssfWorkbook != null) {
			hssfWorkbook.getSheetAt(0).removeRow(row);
		} else {
			xssfWorkbook.getSheetAt(0).removeRow(row);
		}
	}

	public Iterator<Row> iterator() {
		if (hssfWorkbook != null) {
			return hssfWorkbook.getSheetAt(0).iterator();
		} else {
			return xssfWorkbook.getSheetAt(0).iterator();
		}
	}

	public void setOriginalFilename(String originalFilename) {
		this.originalFilename = originalFilename;
	}
}
