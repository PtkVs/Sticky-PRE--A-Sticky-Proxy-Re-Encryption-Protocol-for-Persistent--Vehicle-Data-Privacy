package unipassau.thesis.vehicledatadissemination.util;


import org.springframework.stereotype.Repository;
import unipassau.thesis.vehicledatadissemination.model.MappingPolicyDB;
import org.springframework.data.jpa.repository.JpaRepository;

@Repository

public interface MappingRepoDB extends JpaRepository<MappingPolicyDB, Long> {
    MappingPolicyDB findByHashValue(String hashValue); //SELECT * FROM mapping_policy_db WHERE hash_value = ?; yesari kam garxa yo. The method name is constructed using the keyword findBy followed by the field name HashValue. This tells Spring Data JPA to generate a query to find an entity based on the hashValue field, and returns that value

    MappingPolicyDB findByPolicyName(String policyFilename);
}

