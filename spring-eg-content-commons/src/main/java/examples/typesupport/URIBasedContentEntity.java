package examples.typesupport;

import java.net.URI;

import org.springframework.content.commons.annotations.ContentId;

public class URIBasedContentEntity {

	@ContentId 
	private URI contentId;

	public URI getContentId() {
		return contentId;
	}

	public void setContentId(URI contentId) {
		this.contentId = contentId;
	}
}
