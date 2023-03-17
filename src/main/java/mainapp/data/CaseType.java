package mainapp.data;


public enum CaseType {
	CIVIL("Гражданское дело"),
	PUBLIC("Административное дело"),
	BANKRUPTCY("Дело о банкротстве"),
	ADMIN_OFFENCE("Дело об административном правонарушении"),
	CRIMINAL("Уголовное дело");
	
	public final String label;
		
	private CaseType(String label) {
		 this.label = label;
	}
	
	@Override
	public String toString() {
		return label;
	}
}
