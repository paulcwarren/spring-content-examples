package examples.typesupport;

import org.springframework.content.s3.S3ObjectId;
import org.springframework.content.commons.annotations.ContentId;

public class S3ObjectIdBasedContentEntity {

	@ContentId 
	private S3ObjectId contentId;

	public S3ObjectId getContentId() {
		return contentId;
	}

	public void setContentId(S3ObjectId contentId) {
		this.contentId = contentId;
	}
}
