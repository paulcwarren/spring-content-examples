package examples.models;


import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import org.springframework.content.commons.annotations.ContentId;
import org.springframework.content.commons.annotations.ContentLength;
import org.springframework.content.commons.annotations.MimeType;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class ClaimForm {

   @Id
   @GeneratedValue
   private long id;

   @ContentId
   private String contentId;
   
   @ContentLength
   private long contentLength = 0L;
   
   @MimeType
   private String mimeType = "text/plain";
   
   // Ensure we can handle entities with "computed" getters; i.e. getters that
   // dont have an associated field
   public boolean getIsActive() {
      return true;
   }
}
