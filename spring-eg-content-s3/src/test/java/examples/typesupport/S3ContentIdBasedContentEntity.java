package examples.typesupport;

import org.springframework.content.commons.annotations.ContentId;
import org.springframework.content.s3.S3ContentId;

public class S3ContentIdBasedContentEntity {

	@ContentId 
	private S3ContentId contentId;

	public S3ContentId getContentId() {
		return contentId;
	}

	public void setContentId(S3ContentId contentId) {
		this.contentId = contentId;
	}
}
