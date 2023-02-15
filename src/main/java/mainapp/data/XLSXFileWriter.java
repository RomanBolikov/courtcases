package mainapp.data;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

public class XLSXFileWriter {

	private static final String templatePath = XLSXFileWriter.class.getResource("/mainapp/reportTemplate.xlsx")
			.getPath();

	public static boolean createReport(File directory) throws EncryptedDocumentException, IOException {
		try (Workbook wb = WorkbookFactory.create(new File(templatePath))) {
			Sheet sheet1 = wb.getSheet("Истец_заявитель");
			Sheet sheet2 = wb.getSheet("Отв.(заинт. лицо)");
			Sheet sheet3 = wb.getSheet("Третье лицо");
			Sheet sheet4 = wb.getSheet("Уголовные и КоАП");
			Sheet sheet5 = wb.getSheet("Иные дела на контроле");
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
			String saveName = String.format("Отчет по судам на %s.xlsx", LocalDate.now().format(formatter));
			try (FileOutputStream fileOut = new FileOutputStream(new File(directory, saveName))) {
				wb.write(fileOut);
				return true;
			} catch (IOException ioe) {
				return false;
			}
		} catch (EncryptedDocumentException | IOException e) {
			return false;
		}
	}
}
