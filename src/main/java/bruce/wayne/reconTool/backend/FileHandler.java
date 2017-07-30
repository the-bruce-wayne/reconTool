package bruce.wayne.reconTool.backend;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.commons.io.FilenameUtils;
import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import bruce.wayne.reconTool.backend.utils.LoggersLoader;
import bruce.wayne.reconTool.backend.utils.PropertiesLoader;

/**
 * This class does the comparison of the individual cells in the file It
 * produces a file
 */
public class FileHandler
{
	private File results;
	private LoggersLoader log;
	private PropertiesLoader props;

	public FileHandler(LoggersLoader log, PropertiesLoader props)
	{
		this.log = log;
		this.props = props;

	}

	/*
	 * common contains rows in both file1 and file2 file1 contains rows in
	 * file1 only file2 contains rows in file2 only
	 */
	public File reconcile(File inputFile1, File inputFile2, int file1Column,
			int file2Column)
			throws IOException, EncryptedDocumentException, InvalidFormatException
	{
		
		System.out.println("file 1: " + inputFile1.getName() + " file 2: " + inputFile2.getName());

		// parse files
		Map<String, Row> map1 = this.parseFile(inputFile1, file1Column, true);
		Map<String, Row> map2 = this.parseFile(inputFile2, file2Column, true);

		Map<String, Row> common = new ConcurrentHashMap<String, Row>();
		File file = new File(this.outputFileName());

		// make transaction ID comparison
		for (String key : map1.keySet())
		{
			if (map2.containsKey(key))
			{
				// add to common map
				common.put(key, map1.get(key));
				// remove from file 1 and file 2
				map1.remove(key);
				map2.remove(key);

			}
		}

		// write to file
		this.writeToFile(common, "Common", file);
		this.writeToFile(map1, "file 1 only", file);
		this.writeToFile(map2, "file 2 only", file);
		this.results = file;
		return file;
	}
	
	public ArrayList<String> getColumnHeading(File file, int rowWithColumns) throws IOException
	{
		ArrayList<String> headings = new ArrayList<String>();
		FileInputStream fileToRead = new FileInputStream(file);
		Workbook workbook = null;

		// get file extension
		String fileExtension = FilenameUtils.getExtension(file.getName());

		// create workbook from appropriate file type
		if (fileExtension.equals("xls"))
			workbook = new HSSFWorkbook(fileToRead);
		else if (fileExtension.equals("xlsx"))
			workbook = new XSSFWorkbook(fileToRead);
		else
		{
			fileToRead.close();
			throw new IllegalArgumentException(
					"Received file does not have a standard excel extension.");
		}

		Sheet sheet = workbook.getSheetAt(0);
		Row row = sheet.getRow(rowWithColumns);
		Iterator<Cell> iterator = row.cellIterator();
		while (iterator.hasNext())
			headings.add(this.cellToString(iterator.next()));
		
		fileToRead.close();
		workbook.close();
		
		return headings;
	}

	private Map<String, Row> parseFile(File file, int column, boolean skipHeading) throws IOException
	{
		Map<String, Row> map = new ConcurrentHashMap<String, Row>();
		FileInputStream fileToRead = new FileInputStream(file);
		Workbook workbook = null;

		// get file extension
		String fileExtension = FilenameUtils.getExtension(file.getName());

		// create workbook from appropriate file type
		if (fileExtension.equals("xls"))
			workbook = new HSSFWorkbook(fileToRead);
		else if (fileExtension.equals("xlsx"))
			workbook = new XSSFWorkbook(fileToRead);
		else
		{
			fileToRead.close();
			throw new IllegalArgumentException(
					"Received file does not have a standard excel extension.");
		}

		Sheet sheet = workbook.getSheetAt(0);
		Iterator<Row> rows = sheet.rowIterator();
		Row row = null;
		Cell cell = null;
		String columnId = null;
		while (rows.hasNext())
		{
			row = rows.next();

			// skip first row with column headings
			if (skipHeading && row.getRowNum() == 0)
				continue;

			cell = row.getCell(column);
			columnId = this.cellToString(cell);
			map.put(columnId, row);
		}

		fileToRead.close();
		workbook.close();
		return map;

	}

	private void writeToFile(Map<String, Row> map1, String sheetName,
			File outputFile)
			throws IOException, EncryptedDocumentException, InvalidFormatException
	{

		Workbook workbook;
		if (outputFile.exists())
			workbook = (HSSFWorkbook) WorkbookFactory.create(outputFile);
		else
			workbook = new HSSFWorkbook();

		Sheet sheet = workbook.createSheet(sheetName);

		if (map1.values().size() == 0)
		{
			Row newRow = sheet.createRow(0);
			Cell newCell = newRow.createCell(0);
			newCell.setCellValue(this.props.getEmptyFileMessage());
		} else
		{
			for (Row row : map1.values())
			{
				Row newRow = sheet.createRow(row.getRowNum());
				Iterator<Cell> iterator = row.iterator();
				while (iterator.hasNext())
				{
					Cell cell = iterator.next();
					Cell newCell = newRow.createCell(cell.getColumnIndex());
					newCell.setCellValue(cell.getStringCellValue());
				}
			}
		}

		FileOutputStream fileOut = new FileOutputStream(outputFile);
		workbook.write(fileOut);
		fileOut.flush();
		fileOut.close();
		workbook.close();
	}

	private String cellToString(Cell cell)
	{
		String result = "";
		if(cell == null)
			throw new RuntimeException("Empty cell found");
		switch (cell.getCellTypeEnum())
		{
			case STRING:
				result = String.valueOf(cell);
				break;

			case NUMERIC:
				result = String.valueOf(cell);
				break;

			default:
				throw new RuntimeException("Transaction ID cell type not supported"
						+ cell.getStringCellValue());
		}

		return result;
	}

	private String outputFileName()
	{
		Timestamp tmsp = new Timestamp(System.currentTimeMillis());
		String saveAs = props.getSaveAsName() + tmsp + "."
				+ props.getSaveAsExtension();
		return saveAs;
	}

	public File getResults()
	{
		if (this.results == null)
			throw new RuntimeException("reconciliation not done");

		return this.results;
	}

}
