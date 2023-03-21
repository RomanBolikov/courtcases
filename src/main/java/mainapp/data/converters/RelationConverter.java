package mainapp.data.converters;

import javax.persistence.AttributeConverter;

import mainapp.data.Relation;

public class RelationConverter implements AttributeConverter<Relation, Integer> {

	@Override
	public Integer convertToDatabaseColumn(Relation attribute) {
		return attribute.ordinal();
	}

	@Override
	public Relation convertToEntityAttribute(Integer dbData) {
		return Relation.values()[dbData];
	}

}
