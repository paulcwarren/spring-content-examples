package examples.typesupport;

import java.util.UUID;

import org.springframework.content.commons.annotations.ContentId;

public class UUIDBasedContentEntity {

	@ContentId 
	private UUID contentId;

	public UUID getContentId() {
		return contentId;
	}

	public void setContentId(UUID contentId) {
		this.contentId = contentId;
	}
}
