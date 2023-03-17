package mainapp.helpers;

@SuppressWarnings("serial")
public class SaveCaseException extends Exception {
	
	private String message;
	
	public SaveCaseException(String message) {
		super(message);
		this.message = message;
		System.out.println(this.message);
	}
}
