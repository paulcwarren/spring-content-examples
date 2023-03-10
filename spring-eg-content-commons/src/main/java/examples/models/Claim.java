package examples.models;

import java.util.UUID;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;

import org.springframework.data.mongodb.core.mapping.Document;

@Entity
@Document
public class Claim {

   @Id
   @org.springframework.data.annotation.Id
   private String claimId = UUID.randomUUID().toString();

   private String lastName;
   private String firstName;

   @OneToOne(cascade= CascadeType.ALL)
   private ClaimForm claimForm = new ClaimForm();

   public String getClaimId() {
      return claimId;
   }

   public void setClaimId(String claimId) {
      this.claimId = claimId;
   }

   public String getLastName() {
      return lastName;
   }

   public void setLastName(String lastName) {
      this.lastName = lastName;
   }

   public String getFirstName() {
      return firstName;
   }

   public void setFirstName(String firstName) {
      this.firstName = firstName;
   }

   public ClaimForm getClaimForm() {
      return claimForm;
   }

   public void setClaimForm(ClaimForm claimForm) {
      this.claimForm = claimForm;
   }
}
