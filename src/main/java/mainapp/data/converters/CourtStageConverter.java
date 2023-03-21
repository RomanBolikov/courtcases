package mainapp.data.converters;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

import mainapp.data.CourtStage;

@Converter(autoApply = true)
public class CourtStageConverter implements AttributeConverter<CourtStage, Integer> {

	@Override
	public Integer convertToDatabaseColumn(CourtStage attribute) {
		return attribute.ordinal();
	}

	@Override
	public CourtStage convertToEntityAttribute(Integer dbData) {
		return CourtStage.values()[dbData];
	}
}
