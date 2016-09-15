package com.emc.spring.content.examples;

import org.bson.types.ObjectId;
import org.springframework.core.convert.converter.Converter;

public class LongToObjectIdConverter implements Converter<Long, ObjectId> {

	@Override
	public ObjectId convert(Long source) {
		return new ObjectId(source.toString());
	}

}
