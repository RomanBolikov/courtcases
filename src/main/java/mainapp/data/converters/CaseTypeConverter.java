package mainapp.data.converters;

import javax.persistence.AttributeConverter;

import mainapp.data.CaseType;

public class CaseTypeConverter implements AttributeConverter<CaseType, Integer> {

	@Override
	public Integer convertToDatabaseColumn(CaseType attribute) {
		return attribute.ordinal();
	}

	@Override
	public CaseType convertToEntityAttribute(Integer dbData) {
		return CaseType.values()[dbData];
	}

}
