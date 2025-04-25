package unipassau.thesis.vehicledatadissemination.demo;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;
import org.json.JSONObject;
import org.json.JSONArray;
import org.json.JSONException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import unipassau.thesis.vehicledatadissemination.util.DataHandler;
import unipassau.thesis.vehicledatadissemination.util.OpenPRE;

import java.io.FileReader;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class abacAliceDS {
    private static Logger LOG = LoggerFactory.getLogger(abacAliceDS.class);

    public static String requestDir = System.getProperty("user.dir") + "/requests/";
    public static String csvFilePath = System.getProperty("user.dir") + "/csv/DS1-10.csv";
    public static String pubKey = System.getProperty("user.dir") + "/crypto/alice-public-key";
    public static String dataFolder = System.getProperty("user.dir") + "/data/";
    public static String policyFolder = System.getProperty("user.dir") + "/policies/";

    public static void main(String[] args) {

        // Define the specific request file to process
        String specificRequestFilePath = requestDir + "77.xml";

        // Check if the specific request file exists
        File requestFile = new File(specificRequestFilePath);
        if (!requestFile.exists()) {
            LOG.info("Request file 77.xml not found in directory: " + requestDir);
            return;
        }

        LOG.info("Processing request file: " + requestFile.getName());

        List<String> requestedAttributes = loadRequestedAttributes(requestFile.getPath());

        try (CSVReader reader = new CSVReader(new FileReader(csvFilePath))) {
            List<String[]> records = reader.readAll();
            LOG.info("Total records found: " + records.size());

            int count = 0;
            for (String[] record : records) {
                JSONObject filteredData = new JSONObject();

                // Filter the attributes based on the current request
                for (String attribute : requestedAttributes) {
                    int columnIndex = getColumnIndex(attribute);
                    if (columnIndex != -1) {
                        filteredData.put(attribute, record[columnIndex]);
                    }
                }

                // Encrypt and save the filtered data if any attributes were permitted
                if (filteredData.length() > 0) {
                    LOG.info("Encrypting Data ...");
                    OpenPRE.INSTANCE.encrypt(pubKey, filteredData.toString(), dataFolder + count);

                    LOG.info("Sticking hash of the policy to the data ...");
                    DataHandler.writer(policyFolder + "77.xml", dataFolder + count, count);
                } else {
                    LOG.info("No data to encrypt for this record based on the request.");
                }

                count++;
            }
        } catch (IOException | CsvException e) {
            LOG.error("Error processing the CSV file", e);
        }
    }

    // Helper method to load requested attributes from the specific XML request file
    private static List<String> loadRequestedAttributes(String requestFilePath) {
        List<String> attributes = new ArrayList<>();
        try {
            String content = new String(Files.readAllBytes(Paths.get(requestFilePath)));
            JSONObject requestJson = new JSONObject(content);

            JSONArray categories = requestJson.getJSONObject("Request").getJSONArray("Category");

            // Extract "resource-id" attributes for each resource category
            for (int i = 0; i < categories.length(); i++) {
                JSONObject category = categories.getJSONObject(i);
                if ("urn:oasis:names:tc:xacml:3.0:attribute-category:resource".equals(category.getString("CategoryId"))) {
                    JSONArray attributeArray = category.getJSONArray("Attribute");
                    for (int j = 0; j < attributeArray.length(); j++) {
                        JSONObject attribute = attributeArray.getJSONObject(j);
                        if ("urn:oasis:names:tc:xacml:1.0:resource:resource-id".equals(attribute.getString("AttributeId"))) {
                            attributes.add(attribute.getString("Value"));
                        }
                    }
                }
            }
        } catch (IOException | JSONException e) {
            LOG.error("Error reading request file: " + requestFilePath, e);
        }
        return attributes;
    }

    // Helper method to get CSV column index for a given attribute name
    private static int getColumnIndex(String attribute) {
        switch (attribute) {
            case "header":
                return 0;
            case "timestamp":
                return 1;
            case "antennaAltitudeUnit":
                return 2;
            case "antennaAltitude":
                return 3;
            case "usedSatellites":
                return 4;
            case "quality":
                return 5;
            case "longitude":
                return 6;
            case "latitude":
                return 7;
            default:
                return -1;
        }
    }
}
