package mainapp.helpers;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.ss.util.CellUtil;

import mainapp.data.ACase;
import mainapp.data.Relation;

/**
 * A helper class providing static methods for making an .xlsx report for cases
 * in database. No instances of this class are ever created
 */
public class XLSXFileWriter {

	private static final String templatePath = "/mainapp/reportTemplate.xlsx";

	public static boolean createReport(File directory, List<ACase> list) {
		try (InputStream inputStream = XLSXFileWriter.class.getResourceAsStream(templatePath);
				Workbook wb = WorkbookFactory.create(inputStream)) {
			
			Font font = wb.createFont();
			font.setFontHeightInPoints((short)10);
			font.setFontName("Times New Roman");
			
			CellStyle cs1 = wb.createCellStyle();
			cs1.setAlignment(HorizontalAlignment.CENTER);
			cs1.setVerticalAlignment(VerticalAlignment.CENTER);
			cs1.setBorderBottom(BorderStyle.MEDIUM);
			cs1.setBorderLeft(BorderStyle.MEDIUM);
			cs1.setBorderRight(BorderStyle.MEDIUM);
			cs1.setBorderTop(BorderStyle.MEDIUM);
			cs1.setFont(font);
			
			CellStyle cs2 = wb.createCellStyle();
			cs2.setWrapText(true);
			cs2.setVerticalAlignment(VerticalAlignment.TOP);
			cs2.setFont(font);

			Sheet sheet1 = wb.getSheet("Истец_заявитель");
			
			fillTable(sheet1, 5, list, Relation.PLAINTIFF, cs1);
			
			fillTable(sheet1, 6, list, Relation.DEFENDANT, cs1);
			
			fillTable(sheet1, 7, list, Relation.THIRD_PERSON, cs1);
			
			fillTable(sheet1, 8, list, Relation.CRIMINAL_AND_ADMIN_OFFENCES, cs1);
			
			fillTable(sheet1, 9, list, Relation.CONTROLLED, cs1);
			
			Row totalRow = sheet1.getRow(10);
			Cell sumCell = totalRow.getCell(2);
			if (sumCell == null) {
				sumCell = totalRow.createCell(2);
			}
			sumCell.setCellValue(String.valueOf(list.size()));
			sumCell.setCellStyle(cs1);
			
			List<ACase> list1 = list.stream().filter(c -> c.getRelation() == Relation.PLAINTIFF).toList();
			for (int i = 0, currRow = 14; i < list1.size(); i++) { //!
				ACase acase = list1.get(i);
				Row row = sheet1.createRow(currRow);
				CellUtil.createCell(row, 0, String.valueOf(currRow - 13), cs2); //!
				CellUtil.createCell(row, 1, acase.getDefendant() == null ? "" : acase.getDefendant(), cs2);
				CellUtil.createCell(row, 2, String.format("%s\r\n%s", 
						acase.getCourt().toString().contains("0") ? "-" : acase.getCourt().toString(),
						acase.getCaseNo() == null ? "" : acase.getCaseNo()), cs2);
				CellUtil.createCell(row, 3, acase.getTitle(), cs2);
				CellUtil.createCell(row, 4, acase.getRepr() == null ? "" : acase.getRepr().toString(), cs2);
				CellUtil.createCell(row, 5, acase.getStage().toString(), cs2);
				Cell cell7 = formLastCell(acase, row, 6);
				cell7.setCellStyle(cs2);
				currRow++;
			}

			Sheet sheet2 = wb.getSheet("Отв.(заинт. лицо)");
			List<ACase> list2 = list.stream().filter(c -> c.getRelation() == Relation.DEFENDANT).toList();
			for (int i = 0, currRow = 2; i < list2.size(); i++) {
				ACase acase = list2.get(i);
				Row row = sheet2.createRow(currRow);
				CellUtil.createCell(row, 0, String.valueOf(currRow - 1), cs2);
				CellUtil.createCell(row, 1, acase.getPlaintiff() == null ? "" : acase.getPlaintiff(), cs2);
				CellUtil.createCell(row, 2, String.format("%s\r\n%s", 
						acase.getCourt().toString().contains("0") ? "-" : acase.getCourt().toString(),
						acase.getCaseNo() == null ? "" : acase.getCaseNo()), cs2);
				CellUtil.createCell(row, 3, acase.getTitle(), cs2);
				CellUtil.createCell(row, 4, acase.getRepr() == null ? "" : acase.getRepr().toString(), cs2);
				CellUtil.createCell(row, 5, acase.getStage().toString(), cs2);
				Cell cell7 = formLastCell(acase, row, 6);
				cell7.setCellStyle(cs2);
				currRow++;
			}

			Sheet sheet3 = wb.getSheet("Третье лицо");
			List<ACase> list3 = list.stream().filter(c -> c.getRelation() == Relation.THIRD_PERSON).toList();
			for (int i = 0, currRow = 2; i < list3.size(); i++) {
				ACase acase = list3.get(i);
				Row row = sheet3.createRow(currRow);
				CellUtil.createCell(row, 0, String.valueOf(currRow - 1), cs2);
				CellUtil.createCell(row, 1, acase.getPlaintiff() == null ? "" : acase.getPlaintiff(), cs2);
				CellUtil.createCell(row, 2, acase.getDefendant() == null ? "" : acase.getDefendant(), cs2);
				CellUtil.createCell(row, 3, String.format("%s\r\n%s", 
						acase.getCourt().toString().contains("0") ? "-" : acase.getCourt().toString(),
						acase.getCaseNo() == null ? "" : acase.getCaseNo()), cs2);
				CellUtil.createCell(row, 4, acase.getTitle(), cs2);
				CellUtil.createCell(row, 5, acase.getRepr() == null ? "" : acase.getRepr().toString(), cs2);
				CellUtil.createCell(row, 6, acase.getStage().toString(), cs2);
				Cell cell8 = formLastCell(acase, row, 7);
				cell8.setCellStyle(cs2);
				currRow++;
			}

			Sheet sheet4 = wb.getSheet("Уголовные и КоАП");
			List<ACase> list4 = list.stream().filter(c -> c.getRelation() == Relation.CRIMINAL_AND_ADMIN_OFFENCES)
					.toList();
			for (int i = 0, currRow = 2; i < list4.size(); i++) {
				ACase acase = list4.get(i);
				Row row = sheet4.createRow(currRow);
				CellUtil.createCell(row, 0, String.valueOf(currRow - 1), cs2);
				CellUtil.createCell(row, 1, acase.getDefendant() == null ? "" : acase.getDefendant(), cs2);
				CellUtil.createCell(row, 2, String.format("%s\r\n%s", 
						acase.getCourt().toString().contains("0") ? "-" : acase.getCourt().toString(),
						acase.getCaseNo() == null ? "" : acase.getCaseNo()), cs2);
				CellUtil.createCell(row, 3, acase.getTitle(), cs2);
				CellUtil.createCell(row, 4, acase.getRepr() == null ? "" : acase.getRepr().toString(), cs2);
				CellUtil.createCell(row, 5, acase.getStage().toString(), cs2);
				Cell cell7 = formLastCell(acase, row, 6);
				cell7.setCellStyle(cs2);
				currRow++;
			}

			Sheet sheet5 = wb.getSheet("Иные дела на контроле");
			List<ACase> list5 = list.stream().filter(c -> c.getRelation() == Relation.CONTROLLED).toList();
			for (int i = 0, currRow = 2; i < list5.size(); i++) {
				ACase acase = list5.get(i);
				Row row = sheet5.createRow(currRow);
				CellUtil.createCell(row, 0, String.valueOf(currRow - 1), cs2);
				CellUtil.createCell(row, 1, acase.getPlaintiff() == null ? "" : acase.getPlaintiff(), cs2);
				CellUtil.createCell(row, 2, acase.getDefendant() == null ? "" : acase.getDefendant(), cs2);
				CellUtil.createCell(row, 3, String.format("%s\r\n%s", 
						acase.getCourt().toString().contains("0") ? "-" : acase.getCourt().toString(),
						acase.getCaseNo() == null ? "" : acase.getCaseNo()), cs2);
				CellUtil.createCell(row, 4, acase.getTitle(), cs2);
				CellUtil.createCell(row, 5, acase.getRepr() == null ? "" : acase.getRepr().toString(), cs2);
				CellUtil.createCell(row, 6, acase.getStage().toString(), cs2);
				Cell cell8 = formLastCell(acase, row, 7);
				cell8.setCellStyle(cs2);
				currRow++;
			}
			return saveFile(directory, wb);
		} catch (EncryptedDocumentException | IOException e) {
			return false;
		}
	}
	
	private static void fillTable(Sheet sheet, int rownum, List<ACase> list, Relation relation, CellStyle cs) {
		Row row = sheet.getRow(rownum);
		Cell cell = row.getCell(2);
		if (cell == null) {
			row.createCell(2);
		}
		cell.setCellValue(String.valueOf(list.stream().filter(c -> c.getRelation() == relation).count()));
		cell.setCellStyle(cs);
	}

	private static Cell formLastCell(ACase acase, Row row, int columnIndex) {
		Cell cell = row.createCell(columnIndex);
		LocalDate date = DatePickerConverter.extractLocalDate(acase.getCurrentDate());
		if (date == null) {
			cell.setCellValue(acase.getCurrentState());
		} else {
			DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd.MM.yyyy");
			String dateString = dtf.format(date);
			cell.setCellValue(dateString + "\n" + acase.getCurrentState());
		}
		return cell;
	}

	private static boolean saveFile(File directory, Workbook wb) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
		String saveName = String.format("Отчет по судам на %s.xlsx", LocalDate.now().format(formatter));
		try (FileOutputStream fileOut = new FileOutputStream(new File(directory, saveName))) {
			wb.write(fileOut);
			return true;
		} catch (IOException ioe) {
			return false;
		}
	}
}
