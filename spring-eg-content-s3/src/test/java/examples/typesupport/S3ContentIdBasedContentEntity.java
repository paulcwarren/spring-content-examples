package examples.typesupport;

import java.math.BigInteger;

import org.springframework.content.commons.annotations.ContentId;

public class BigIntegerBasedContentEntity {

	@ContentId 
	private BigInteger contentId;

	public BigInteger getContentId() {
		return contentId;
	}

	public void setContentId(BigInteger contentId) {
		this.contentId = contentId;
	}
}
