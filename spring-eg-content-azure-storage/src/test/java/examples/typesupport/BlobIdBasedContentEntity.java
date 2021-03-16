package examples.typesupport;

import org.springframework.content.azure.config.BlobId;
import org.springframework.content.commons.annotations.ContentId;

public class BlobIdBasedContentEntity {

	@ContentId
	private BlobId contentId;

	public BlobId getContentId() {
		return contentId;
	}

	public void setContentId(BlobId contentId) {
		this.contentId = contentId;
	}
}
