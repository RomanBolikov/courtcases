package mainapp.helpers;

@SuppressWarnings("serial")
public class SaveEntityException extends Exception {
	
	private String message;
	
	public SaveEntityException(String message) {
		super(message);
		this.message = message;
		System.out.println(this.message);
	}
}
