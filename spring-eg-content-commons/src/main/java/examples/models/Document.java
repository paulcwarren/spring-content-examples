package examples.models;

import org.springframework.content.commons.annotations.ContentId;
import org.springframework.content.commons.annotations.ContentLength;
import org.springframework.content.commons.annotations.MimeType;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import java.util.UUID;

@Entity
@org.springframework.data.mongodb.core.mapping.Document
public class Document {

    @Id
//    @GeneratedValue
    @org.springframework.data.annotation.Id
    private String id = UUID.randomUUID().toString();

    @ContentId
    private String contentId;

    @ContentLength
    private Long contentLen;

    @MimeType String mimeType;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getContentId() {
        return contentId;
    }

    public void setContentId(String contentId) {
        this.contentId = contentId;
    }

    public Long getContentLen() {
        return contentLen;
    }

    public void setContentLen(Long contentLen) {
        this.contentLen = contentLen;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }
}
