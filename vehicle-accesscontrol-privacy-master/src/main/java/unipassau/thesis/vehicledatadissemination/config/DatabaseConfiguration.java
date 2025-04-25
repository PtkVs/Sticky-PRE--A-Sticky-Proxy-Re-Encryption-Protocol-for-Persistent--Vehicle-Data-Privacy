package unipassau.thesis.vehicledatadissemination.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import unipassau.thesis.vehicledatadissemination.util.MappingRepoDB;
import unipassau.thesis.vehicledatadissemination.model.MappingPolicyDB;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;

@Component
public class DatabaseConfiguration {

    private final Logger logger = LoggerFactory.getLogger(PolicyConfiguration.class);

    @Autowired
    private MappingRepoDB maprepo;



    //@Autowired
    // public DatabaseConfiguration(MappingRepoDB maprepo){
    //     this.maprepo = maprepo;
    //}


   public void saveMappings(String hashValue, String policyName) {


        //For storing the metadata i.e path of the request file along with hashValue and policyName in the DB
       final String Request_Direcotry_Path = System.getProperty("user.dir")+"/requests/";
       logger.info("Saving request file paths to database.");

       File requestDirectory = new File(Request_Direcotry_Path);
       File[] requestFiles = requestDirectory.listFiles();

       if (requestFiles != null) {
           for (File file : requestFiles) {
               //To avoid confusion of having multiple names and values
               if(file.getName().equals(policyName)) {
                   //For Saving hashValue, PolicyName request path in the DB
                   MappingPolicyDB mapping = new MappingPolicyDB();
                   mapping.setHashValue(hashValue);
                   mapping.setPolicyName(policyName);
                   mapping.setPolicyReqPath(file.getAbsolutePath()); // Save the absolute path of the request file
                   maprepo.save(mapping);
               }

           }
       } else {
           logger.warn("No request files found in directory: {}", Request_Direcotry_Path);
       }
       System.out.println("Saved to DB");
   }


    public void printAllMappings() {
        List<MappingPolicyDB> mappings = maprepo.findAll();
        System.out.println("All mappings in the database:");
        for (MappingPolicyDB mapping : mappings) {
            System.out.println("HashValue: " + mapping.getHashValue() + ", PolicyName: " + mapping.getPolicyName() + ", DirectoryPath: " + mapping.getPolicyReqPath());
        }
    }

    public boolean authenticate(String hashValue) {

        //object created so that getter methods of the MappingPolicyDB classes can be invoked, check its availability and compare for authentication
       MappingPolicyDB mapped = maprepo.findByHashValue(hashValue); //mapped vaneko db bata ako hash value vayo where hashValue vaneko Bob bata ako request ko value
       if(mapped !=null && mapped.getHashValue().equals(hashValue)){

           // Hash value match a record in the database
           return true;
       }  else {
           // No match found in the database
           return false;
       }
    }

    public MappingPolicyDB getMappingByHashValue(String hashValue) {
        return maprepo.findByHashValue(hashValue);
    }
}






