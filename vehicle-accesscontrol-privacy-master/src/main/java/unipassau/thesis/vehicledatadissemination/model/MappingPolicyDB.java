package unipassau.thesis.vehicledatadissemination.model;

import javax.persistence.*;

@Entity
@Table(name = "Mapping_Policy_DB")
public class MappingPolicyDB {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long ID;
    private String hashValue;
    private String policyName;

    @Lob
    private String policyRequestPath;


    public Long getID() {
        return ID;
    }

    public void setID(Long ID) {
        this.ID = ID;
    }

    public String getHashValue() {
        return hashValue;
    }

    public void setHashValue(String hashValue) {
        this.hashValue = hashValue;
    }

    public String getPolicyName() {
        return policyName;
    }

    public void setPolicyName(String policyName) {
        this.policyName = policyName;
    }

    public String getPolicyReqPath(){
        return policyRequestPath;
    }

    public void setPolicyReqPath(String policyRequest){
        this.policyRequestPath = policyRequest;
    }
}

