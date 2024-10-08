package support.cmis;

import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.ManyToOne;
import javax.persistence.Version;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import org.springframework.content.cmis.CmisDescription;
import org.springframework.content.cmis.CmisName;
import org.springframework.content.cmis.CmisReference;
import org.springframework.content.cmis.CmisReferenceType;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@NoArgsConstructor
@Getter
@Setter
public class BaseObject {

	@javax.persistence.Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long Id;

	@CmisName
	private String name;

	@CmisDescription
	private String description;

	@CreatedBy
	private String createdBy;

	@CreatedDate
	private Date createdDate;

	@LastModifiedBy
	private String lastModifiedBy;

	@LastModifiedDate
	private Date lastModifiedDate;

	@Version
	private Long vstamp;

	@CmisReference(type = CmisReferenceType.Parent)
	@ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	private Folder parent;

	public BaseObject(String name) {
		this.name = name;
	}
}
