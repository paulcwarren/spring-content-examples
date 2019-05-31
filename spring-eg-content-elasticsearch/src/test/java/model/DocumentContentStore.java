package model;

import org.springframework.content.commons.repository.ContentStore;
import org.springframework.content.commons.search.Searchable;

public interface DocumentContentStore extends ContentStore<Document, String>, Searchable<String> {
	//
}
