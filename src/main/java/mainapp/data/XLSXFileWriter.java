package mainapp.data;

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

/**
 * A helper class providing static methods for making an .xlsx report for cases
 * in database. No objects of this class are ever instantiated
 */
public class XLSXFileWriter {

	private static final String templatePath = "/mainapp/reportTemplate.xlsx";

	public static boolean createReport(File directory, DataModel model, boolean includeArchive) {
		try (InputStream is = XLSXFileWriter.class.getResourceAsStream(templatePath);
			Workbook wb = WorkbookFactory.create(is)) {
			CellStyle cs = wb.createCellStyle();
			cs.setWrapText(true);
			cs.setVerticalAlignment(VerticalAlignment.TOP);

			Sheet sheet1 = wb.getSheet("Истец_заявитель");
			Relation relation1 = model.getRelationRepo().findById(1).get();
			List<ACase> list1 = model.getCaseRepo().findByRelation(relation1);
			for (int i = 0, currRow = 2; i < list1.size(); i++) {
				ACase acase = list1.get(i);
				if (!includeArchive && acase.isArchive())
					continue;
				Row row = sheet1.createRow(currRow);
				CellUtil.createCell(row, 0, String.valueOf(currRow - 1), cs);
				CellUtil.createCell(row, 1, acase.getDefendant(), cs);
				CellUtil.createCell(row, 2, String.format("%s\r\n%s", acase.getCourt().toString(),
						acase.getCaseNo() == null ? "" : acase.getCaseNo()), cs);
				CellUtil.createCell(row, 3, acase.getTitle(), cs);
				CellUtil.createCell(row, 4, acase.getRepr().toString(), cs);
				CellUtil.createCell(row, 5, acase.getStage().toString(), cs);
				Cell cell7 = formLastCell(acase, row, 6);
				cell7.setCellStyle(cs);
				currRow++;
			}

			Sheet sheet2 = wb.getSheet("Отв.(заинт. лицо)");
			Relation relation2 = model.getRelationRepo().findById(2).get();
			List<ACase> list2 = model.getCaseRepo().findByRelation(relation2);
			for (int i = 0, currRow = 2; i < list2.size(); i++) {
				ACase acase = list2.get(i);
				if (!includeArchive && acase.isArchive())
					continue;
				Row row = sheet2.createRow(currRow);
				CellUtil.createCell(row, 0, String.valueOf(currRow - 1), cs);
				CellUtil.createCell(row, 1, acase.getPlaintiff(), cs);
				CellUtil.createCell(row, 2, String.format("%s\r\n%s", acase.getCourt().toString(),
						acase.getCaseNo() == null ? "" : acase.getCaseNo()), cs);
				CellUtil.createCell(row, 3, acase.getTitle(), cs);
				CellUtil.createCell(row, 4, acase.getRepr().toString(), cs);
				CellUtil.createCell(row, 5, acase.getStage().toString(), cs);
				Cell cell7 = formLastCell(acase, row, 6);
				cell7.setCellStyle(cs);
				currRow++;
			}

			Sheet sheet3 = wb.getSheet("Третье лицо");
			Relation relation3 = model.getRelationRepo().findById(3).get();
			List<ACase> list3 = model.getCaseRepo().findByRelation(relation3);
			for (int i = 0, currRow = 2; i < list3.size(); i++) {
				ACase acase = list3.get(i);
				if (!includeArchive && acase.isArchive())
					continue;
				Row row = sheet3.createRow(currRow);
				CellUtil.createCell(row, 0, String.valueOf(currRow - 1), cs);
				CellUtil.createCell(row, 1, acase.getPlaintiff(), cs);
				CellUtil.createCell(row, 2, acase.getDefendant(), cs);
				CellUtil.createCell(row, 3, String.format("%s\r\n%s", acase.getCourt().toString(),
						acase.getCaseNo() == null ? "" : acase.getCaseNo()), cs);
				CellUtil.createCell(row, 4, acase.getTitle(), cs);
				CellUtil.createCell(row, 5, acase.getRepr().toString(), cs);
				CellUtil.createCell(row, 6, acase.getStage().toString(), cs);
				Cell cell8 = formLastCell(acase, row, 7);
				cell8.setCellStyle(cs);
				currRow++;
			}

			Sheet sheet4 = wb.getSheet("Уголовные и КоАП");
			Relation relation4 = model.getRelationRepo().findById(5).get();
			List<ACase> list4 = model.getCaseRepo().findByRelation(relation4);
			for (int i = 0, currRow = 2; i < list4.size(); i++) {
				ACase acase = list4.get(i);
				if (!includeArchive && acase.isArchive())
					continue;
				Row row = sheet4.createRow(currRow);
				CellUtil.createCell(row, 0, String.valueOf(currRow - 1), cs);
				CellUtil.createCell(row, 1, acase.getDefendant(), cs);
				CellUtil.createCell(row, 2, String.format("%s\r\n%s", acase.getCourt().toString(),
						acase.getCaseNo() == null ? "" : acase.getCaseNo()), cs);
				CellUtil.createCell(row, 3, acase.getTitle(), cs);
				CellUtil.createCell(row, 4, acase.getRepr().toString(), cs);
				CellUtil.createCell(row, 5, acase.getStage().toString(), cs);
				Cell cell7 = formLastCell(acase, row, 6);
				cell7.setCellStyle(cs);
				currRow++;
			}

			Sheet sheet5 = wb.getSheet("Иные дела на контроле");
			Relation relation5 = model.getRelationRepo().findById(4).get();
			List<ACase> list5 = model.getCaseRepo().findByRelation(relation5);
			for (int i = 0, currRow = 2; i < list5.size(); i++) {
				ACase acase = list5.get(i);
				if (!includeArchive && acase.isArchive())
					continue;
				Row row = sheet5.createRow(currRow);
				CellUtil.createCell(row, 0, String.valueOf(currRow - 1), cs);
				CellUtil.createCell(row, 1, acase.getPlaintiff(), cs);
				CellUtil.createCell(row, 2, acase.getDefendant(), cs);
				CellUtil.createCell(row, 3, String.format("%s\r\n%s", acase.getCourt().toString(),
						acase.getCaseNo() == null ? "" : acase.getCaseNo()), cs);
				CellUtil.createCell(row, 4, acase.getTitle(), cs);
				CellUtil.createCell(row, 5, acase.getRepr().toString(), cs);
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
		if (date == null)
			cell.setCellValue(acase.getCurrentState());
		else {
			DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd.MM.yyyy");
			String dateString = dtf.format(date);
			cell.setCellValue(String.format("%s\r\n%s", dateString, acase.getCurrentState()));
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
