package mainapp.data;

public enum Relation {
	PLAINTIFF("Дела, по которым наша компания является истцом (заявителем)"),
	DEFENDANT("Дела, по которым наша компания является ответчиком (лицом, действия которого оспариваются)"),
	THIRD_PERSON("Дела, по которым наша компания является третьим (заинтересованным) лицом"),
	CONTROLLED("Дела на контроле"),
	CRIMINAL_AND_ADMIN_OFFENCES("Уголовные дела и дела об админ. правонарушениях");
	
	private final String label;

    private Relation(String label) {
        this.label = label;
    }
    
    @Override
	public String toString() {
		return label;
	}
}
