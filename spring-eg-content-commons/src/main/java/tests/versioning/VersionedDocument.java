package tests.versioning;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Version;

import org.springframework.content.commons.annotations.ContentId;
import org.springframework.content.commons.annotations.ContentLength;
import org.springframework.content.commons.annotations.MimeType;
import org.springframework.versions.AncestorId;
import org.springframework.versions.AncestorRootId;
import org.springframework.versions.LockOwner;
import org.springframework.versions.SuccessorId;
import org.springframework.versions.VersionLabel;
import org.springframework.versions.VersionNumber;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter
@Setter
@EqualsAndHashCode
@ToString
public class VersionedDocument {

    @Id
    @GeneratedValue
    private Long id;

    @Version
    private Long vstamp;

    @ContentId
    private String contentId;

    @ContentLength
    private long contentLen;

    @MimeType
    private String mimeType;

    @LockOwner
    private String lockOwner;

    @AncestorId
    private Long ancestorId;

    @AncestorRootId
    private Long ancestralRootId;

    @SuccessorId
    private Long successorId;

    @VersionNumber
    private String version;

    @VersionLabel
    private String label;

    private String data;

    public VersionedDocument() {
    }

    public VersionedDocument(VersionedDocument doc) {
        this.setContentId(doc.getContentId());
        this.setContentLen(doc.getContentLen());
        this.setMimeType(doc.getMimeType());
        this.setLockOwner(doc.getLockOwner());
        this.setData(doc.getData());
    }
}
