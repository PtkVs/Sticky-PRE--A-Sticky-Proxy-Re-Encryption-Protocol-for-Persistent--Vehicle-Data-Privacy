package unipassau.thesis.vehicledatadissemination.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import unipassau.thesis.vehicledatadissemination.config.PolicyFile;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.security.Principal;
import java.util.Properties;

@Service
public class PDPService {

    private String pdpServerUrl;

    @Autowired
    private org.springframework.core.env.Environment env;

    public boolean updateRemotePDPServer(String requestContent) {
        loadProperties();

        boolean decisionEnc = false;
        try {

            // Construct the URL for the updatePDPConfig endpoint
            URL url = new URL(pdpServerUrl + "application/json");

            // Open a connection to the URL
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            // Set the request method to POST
            connection.setRequestMethod("POST");

            // Enable input/output streams
            connection.setDoOutput(true);


            // Set the content type to XML
            connection.setRequestProperty("Content-Type", "application/json");

            // Convert the normal string to JSON
            String jsonData = requestContent;


            try (OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream(), StandardCharsets.UTF_8)) {
                writer.write(jsonData);
                writer.flush();
            }


            // Get the response code
            int responseCode = connection.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK) {

                // Process the response based on PDP decision
                String pdpDecision = convertStreamToString(connection.getInputStream());
                pdpDecision = pdpDecision.trim();

                if ("Permit".equals(pdpDecision)) {
                  //  System.out.println("PDP's decision is PERMIT. So, Proceeding with encryption.");
                    decisionEnc = true;

                    // Close the connection
                    connection.disconnect();

                } else if ("Deny".equals(pdpDecision)) {
                    System.out.println("PDP denied the request. Aborting encryption.");


                    // Close the connection
                    connection.disconnect();

                    System.exit(0);


                }
            } else {
                System.out.println("Failed to update PDP config. Response Code: " + responseCode + "The program will exit now.");

                // Close the connection
                connection.disconnect();

                System.exit(0);

            }


        } catch (Exception e) {
            e.printStackTrace();
        }
        return decisionEnc;
    }

    public static String convertStreamToString(InputStream inputStream) throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line).append("\n");
            }
        }
        return stringBuilder.toString();
    }




    private String loadProperties() {
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("application.properties")) {
            Properties properties = new Properties();
            if (input != null) {
                properties.load(input);
                pdpServerUrl = properties.getProperty("pdp.location");

            } else {
                System.out.println("Sorry, unable to Connect to PDP server");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return pdpServerUrl;
    }




   /* private Document convertStringToXML(String policyContent) throws ParserConfigurationException, IOException, SAXException {
        // Create a DocumentBuilder
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();

        // Parse the policyContent string to create a Document
        Document document = builder.parse(new InputSource(new ByteArrayInputStream(policyContent.getBytes())));

        return document;
    }

    private String serializeXML(Document document) throws TransformerException {
        // Create a Transformer
        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer transformer = tf.newTransformer();

        // Serialize the XML document to a string
        StringWriter writer = new StringWriter();
        transformer.transform(new DOMSource(document), new StreamResult(writer));

        return writer.getBuffer().toString();
    }


    public String convertToJson(String normalString) {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode objectNode = objectMapper.createObjectNode();

        objectNode.put("key", normalString);

        try {
            return objectMapper.writeValueAsString(objectNode);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    } */
}