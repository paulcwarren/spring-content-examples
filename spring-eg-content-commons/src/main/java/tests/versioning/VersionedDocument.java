package tests.versioning;

import org.springframework.content.commons.annotations.ContentId;
import org.springframework.content.commons.annotations.ContentLength;
import org.springframework.content.commons.annotations.MimeType;
import org.springframework.versions.AncestorId;
import org.springframework.versions.AncestorRootId;
import org.springframework.versions.LockOwner;
import org.springframework.versions.VersionLabel;
import org.springframework.versions.VersionNumber;
import org.springframework.versions.VersionStatus;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Version;
import java.util.UUID;

@Entity
public class VersionedDocument {

    @Id
    @GeneratedValue
    private Long id;

    @Version
    private Long vstamp;

    @ContentId
    private UUID contentId;

    @ContentLength
    private int contentLen;

    @MimeType
    private String mimeType;

    @LockOwner
    private String lockOwner;

    @AncestorId
    private Long ancestorId;

    @AncestorRootId
    private Long ancestralRootId;

    @VersionNumber
    private String version;

    @VersionLabel
    private String label;

    @VersionStatus
    private boolean latest;

    private String data;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getVstamp() {
        return vstamp;
    }

    public void setVstamp(Long vstamp) {
        this.vstamp = vstamp;
    }

    public UUID getContentId() {
        return contentId;
    }

    public void setContentId(UUID contentId) {
        this.contentId = contentId;
    }

    public int getContentLen() {
        return contentLen;
    }

    public void setContentLen(int contentLen) {
        this.contentLen = contentLen;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public String getLockOwner() {
        return lockOwner;
    }

    public void setLockOwner(String lockOwner) {
        this.lockOwner = lockOwner;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public boolean isLatest() {
        return latest;
    }

    public void setLatest(boolean latest) {
        this.latest = latest;
    }

    public Long getAncestralRootId() {
        return ancestralRootId;
    }

    public void setAncestralRootId(Long ancestralRootId) {
        this.ancestralRootId = ancestralRootId;
    }

    public Long getAncestorId() {
        return ancestorId;
    }

    public void setAncestorId(Long ancestorId) {
        this.ancestorId = ancestorId;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof VersionedDocument)) return false;

        VersionedDocument that = (VersionedDocument) o;

        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public String toString() {
        return "VersionedDocument{" +
                "id=" + id +
                ":" + vstamp +
                '}';
    }
}
