package mainapp.data;

public enum CourtStage {
	
	PRE_COURT("досудебное производство"), 
	FIRST("1 инстанция"),
	SECOND("2 инстанция"),
	THIRD("3 инстанция"),
	SUPREME_COURT("Верховный Суд РФ");

	private final String label;

    private CourtStage(String label) {
        this.label = label;
    }
    
    @Override
	public String toString() {
		return label;
	}
}
