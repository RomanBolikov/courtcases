package mainapp.helpers;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
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
 * in database. No objects of this class are ever instantiated
 */
public class XLSXFileWriter {

	private static final String templatePath = "/mainapp/reportTemplate.xlsx";

	public static boolean createReport(File directory, List<ACase> list) {
		try (InputStream inputStream = XLSXFileWriter.class.getResourceAsStream(templatePath);
				Workbook wb = WorkbookFactory.create(inputStream)) {
			CellStyle cs = wb.createCellStyle();
			cs.setWrapText(true);
			cs.setVerticalAlignment(VerticalAlignment.TOP);

			Sheet sheet1 = wb.getSheet("Истец_заявитель");
			List<ACase> list1 = list.stream().filter(c -> c.getRelation() == Relation.PLAINTIFF).toList();
			for (int i = 0, currRow = 2; i < list1.size(); i++) {
				ACase acase = list1.get(i);
				Row row = sheet1.createRow(currRow);
				CellUtil.createCell(row, 0, String.valueOf(currRow - 1), cs);
				CellUtil.createCell(row, 1, acase.getDefendant() == null ? "" : acase.getDefendant(), cs);
				CellUtil.createCell(row, 2, String.format("%s\r\n%s", acase.getCourt().toString(),
						acase.getCaseNo() == null ? "" : acase.getCaseNo()), cs);
				CellUtil.createCell(row, 3, acase.getTitle(), cs);
				CellUtil.createCell(row, 4, acase.getRepr() == null ? "" : acase.getRepr().toString(), cs);
				CellUtil.createCell(row, 5, acase.getStage().toString(), cs);
				Cell cell7 = formLastCell(acase, row, 6);
				cell7.setCellStyle(cs);
				currRow++;
			}

			Sheet sheet2 = wb.getSheet("Отв.(заинт. лицо)");
			List<ACase> list2 = list.stream().filter(c -> c.getRelation() == Relation.DEFENDANT).toList();
			for (int i = 0, currRow = 2; i < list2.size(); i++) {
				ACase acase = list2.get(i);
				Row row = sheet2.createRow(currRow);
				CellUtil.createCell(row, 0, String.valueOf(currRow - 1), cs);
				CellUtil.createCell(row, 1, acase.getPlaintiff() == null ? "" : acase.getPlaintiff(), cs);
				CellUtil.createCell(row, 2, String.format("%s\r\n%s", acase.getCourt().toString(),
						acase.getCaseNo() == null ? "" : acase.getCaseNo()), cs);
				CellUtil.createCell(row, 3, acase.getTitle(), cs);
				CellUtil.createCell(row, 4, acase.getRepr() == null ? "" : acase.getRepr().toString(), cs);
				CellUtil.createCell(row, 5, acase.getStage().toString(), cs);
				Cell cell7 = formLastCell(acase, row, 6);
				cell7.setCellStyle(cs);
				currRow++;
			}

			Sheet sheet3 = wb.getSheet("Третье лицо");
			List<ACase> list3 = list.stream().filter(c -> c.getRelation() == Relation.THIRD_PERSON).toList();
			for (int i = 0, currRow = 2; i < list3.size(); i++) {
				ACase acase = list3.get(i);
				Row row = sheet3.createRow(currRow);
				CellUtil.createCell(row, 0, String.valueOf(currRow - 1), cs);
				CellUtil.createCell(row, 1, acase.getPlaintiff() == null ? "" : acase.getPlaintiff(), cs);
				CellUtil.createCell(row, 2, acase.getDefendant() == null ? "" : acase.getDefendant(), cs);
				CellUtil.createCell(row, 3, String.format("%s\r\n%s", acase.getCourt().toString(),
						acase.getCaseNo() == null ? "" : acase.getCaseNo()), cs);
				CellUtil.createCell(row, 4, acase.getTitle(), cs);
				CellUtil.createCell(row, 5, acase.getRepr() == null ? "" : acase.getRepr().toString(), cs);
				CellUtil.createCell(row, 6, acase.getStage().toString(), cs);
				Cell cell8 = formLastCell(acase, row, 7);
				cell8.setCellStyle(cs);
				currRow++;
			}

			Sheet sheet4 = wb.getSheet("Уголовные и КоАП");
			List<ACase> list4 = list.stream().filter(c -> c.getRelation() == Relation.CRIMINAL_AND_ADMIN_OFFENCES)
					.toList();
			for (int i = 0, currRow = 2; i < list4.size(); i++) {
				ACase acase = list4.get(i);
				Row row = sheet4.createRow(currRow);
				CellUtil.createCell(row, 0, String.valueOf(currRow - 1), cs);
				CellUtil.createCell(row, 1, acase.getDefendant() == null ? "" : acase.getDefendant(), cs);
				CellUtil.createCell(row, 2, String.format("%s\r\n%s", acase.getCourt().toString(),
						acase.getCaseNo() == null ? "" : acase.getCaseNo()), cs);
				CellUtil.createCell(row, 3, acase.getTitle(), cs);
				CellUtil.createCell(row, 4, acase.getRepr() == null ? "" : acase.getRepr().toString(), cs);
				CellUtil.createCell(row, 5, acase.getStage().toString(), cs);
				Cell cell7 = formLastCell(acase, row, 6);
				cell7.setCellStyle(cs);
				currRow++;
			}

			Sheet sheet5 = wb.getSheet("Иные дела на контроле");
			List<ACase> list5 = list.stream().filter(c -> c.getRelation() == Relation.CONTROLLED).toList();
			for (int i = 0, currRow = 2; i < list5.size(); i++) {
				ACase acase = list5.get(i);
				Row row = sheet5.createRow(currRow);
				CellUtil.createCell(row, 0, String.valueOf(currRow - 1), cs);
				CellUtil.createCell(row, 1, acase.getPlaintiff() == null ? "" : acase.getPlaintiff(), cs);
				CellUtil.createCell(row, 2, acase.getDefendant() == null ? "" : acase.getDefendant(), cs);
				CellUtil.createCell(row, 3, String.format("%s\r\n%s", acase.getCourt().toString(),
						acase.getCaseNo() == null ? "" : acase.getCaseNo()), cs);
				CellUtil.createCell(row, 4, acase.getTitle(), cs);
				CellUtil.createCell(row, 5, acase.getRepr() == null ? "" : acase.getRepr().toString(), cs);
				CellUtil.createCell(row, 6, acase.getStage().toString(), cs);
				Cell cell8 = formLastCell(acase, row, 7);
				cell8.setCellStyle(cs);
				currRow++;
			}
			return saveFile(directory, wb);
		} catch (EncryptedDocumentException | IOException e) {
			return false;
		}
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
