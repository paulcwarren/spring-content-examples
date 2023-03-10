package model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import org.springframework.content.commons.annotations.ContentId;

@Entity
@NoArgsConstructor
@Getter
@Setter
public class Document {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@ContentId
	private String contentId;

	private String title;
	private String author;
}
