package cyber.tf.authzforcepdpservice.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import cyber.tf.authzforcepdpservice.util.Encoder;

//orginal
import cyber.tf.authzforcepdpservice.util.Encoder;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.Request;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.Response;
import org.json.JSONObject;
import org.ow2.authzforce.core.pdp.api.CloseablePdpEngine;
import org.ow2.authzforce.core.pdp.api.DecisionRequestPreprocessor;
import org.ow2.authzforce.core.pdp.api.DecisionResultPostprocessor;
import org.ow2.authzforce.core.pdp.api.PdpEngine;
import org.ow2.authzforce.core.pdp.api.io.BaseXacmlJaxbResultPostprocessor;
import org.ow2.authzforce.core.pdp.api.io.PdpEngineInoutAdapter;
import org.ow2.authzforce.core.pdp.api.value.AttributeValueFactoryRegistry;
import org.ow2.authzforce.core.pdp.impl.BasePdpEngine;
import org.ow2.authzforce.core.pdp.impl.PdpEngineConfiguration;
import org.ow2.authzforce.core.pdp.impl.io.PdpEngineAdapters;
import org.ow2.authzforce.core.pdp.impl.io.SingleDecisionXacmlJaxbRequestPreprocessor;
import org.ow2.authzforce.core.pdp.io.xacml.json.BaseXacmlJsonResultPostprocessor;
import org.ow2.authzforce.core.pdp.io.xacml.json.SingleDecisionXacmlJsonRequestPreprocessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;
import java.io.*;
import java.util.HashMap;
import java.util.Map;
//org till here


@Service
public class PDPService {
    private final Logger logger = LoggerFactory.getLogger(PDPService.class);

    private final CloseablePdpEngine engine;

    private final PdpEngineInoutAdapter<Request, Response> xmlAdapter;
    private final PdpEngineInoutAdapter<JSONObject, JSONObject> jsonAdapter;


    Logger LOG = LoggerFactory.getLogger(PDPService.class); //**

    @Autowired
    private HashMap<String, String> policyMap; //**

    @Autowired
    org.springframework.core.env.Environment env;  //**

    @Autowired
    public PDPService(PdpEngineConfiguration pdpEngineConfiguration) throws IOException {
      //  logger.info("Loading XACML PDP Engine.");
        engine = new BasePdpEngine(pdpEngineConfiguration);
      //  logger.info("Loaded XACML PDP Engine.");

     //   logger.info("Loading XACML XML Adapter.");
        this.xmlAdapter = PdpEngineAdapters.newInoutAdapter(Request.class,
                Response.class,
                engine,
                pdpEngineConfiguration.getInOutProcChains(),
                (extraPdpFeatures) -> SingleDecisionXacmlJaxbRequestPreprocessor.LaxVariantFactory.INSTANCE.getInstance(pdpEngineConfiguration.getAttributeValueFactoryRegistry(),
                        pdpEngineConfiguration.isStrictAttributeIssuerMatchEnabled(),
                        pdpEngineConfiguration.isXPathEnabled(),
                        extraPdpFeatures),
                () -> new BaseXacmlJaxbResultPostprocessor(pdpEngineConfiguration.getClientRequestErrorVerbosityLevel()));
     //   logger.info("Loaded XACML XML Adapter.");

     //   logger.info("Loading XACML JSON Adapter.");
        this.jsonAdapter = PdpEngineAdapters.newInoutAdapter(
                JSONObject.class,
                JSONObject.class,
                engine,
                pdpEngineConfiguration.getInOutProcChains(),
                (extraPdpFeatures) -> SingleDecisionXacmlJsonRequestPreprocessor.LaxVariantFactory.INSTANCE.getInstance(pdpEngineConfiguration.getAttributeValueFactoryRegistry(),
                        pdpEngineConfiguration.isStrictAttributeIssuerMatchEnabled(),
                        pdpEngineConfiguration.isXPathEnabled(),
                        extraPdpFeatures),
                () -> new BaseXacmlJsonResultPostprocessor(pdpEngineConfiguration.getClientRequestErrorVerbosityLevel()));
     //   logger.info("Loaded XACML JSON Adapter.");
    }

    public PdpEngineInoutAdapter<Request, Response> getXMLAdapter() {
        return xmlAdapter;
    }

    public PdpEngineInoutAdapter<JSONObject, JSONObject> getJSONAdapter() {
        return jsonAdapter;
    }

}


    //Maile gareko changes from here

/*
        public void setPdpConfigFile(byte[] hash) { //polcies load form here instead of suru mai as policies ko hash nai mileko xain tryt this and if not pdp le kun policies load gariraxa ra kun policies ko hash liiraxa find
            try {
                String policy = policyMap.get(Encoder.bytesToHex(hash));
                if (policy == null) {
                    throw new IllegalArgumentException("Policy not found for the given hash: " + Encoder.bytesToHex(hash));
                }

                String pdpConfigPath = env.getProperty("pdp.config.path");
                if (pdpConfigPath == null) {
                    throw new IllegalArgumentException("PDP config path not configured.");
                }

                File pdpConfigFile = new File(pdpConfigPath);
                if (!pdpConfigFile.exists()) {
                    throw new IOException("PDP config file does not exist: " + pdpConfigPath);
                }

                DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
                dbFactory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
                dbFactory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
                DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
                Document doc = dBuilder.parse(pdpConfigFile);

                XPathFactory xpathFactory = XPathFactory.newInstance();
                XPath xpath = xpathFactory.newXPath();
                XPathExpression policyLocationExpr = xpath.compile("/pdp/policyProvider/policyLocation");
                Node policyLocationNode = (Node) policyLocationExpr.evaluate(doc, XPathConstants.NODE);

                String currentPolicyLocation = policyLocationNode.getTextContent();
                String expectedPolicyLocation = "policies/" + policy;
                if (!currentPolicyLocation.equals(expectedPolicyLocation)) {
                    policyLocationNode.setTextContent(expectedPolicyLocation);
                    saveDocument(doc, pdpConfigFile);
                    System.out.println("Updated PDP config file with policy: " + policy);
                } else {
                    System.out.println("PDP config file is already configured with the correct policy: " + policy);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        private void saveDocument(Document doc, File outputFile) throws IOException, TransformerException {
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");

            DOMSource source = new DOMSource(doc);
            try (FileOutputStream fos = new FileOutputStream(outputFile);
                 OutputStreamWriter osw = new OutputStreamWriter(fos, StandardCharsets.UTF_8);
                 PrintWriter writer = new PrintWriter(osw)) {
                StreamResult result = new StreamResult(writer);
                transformer.transform(source, result);
            }
        }
    }
   /* public void setPdpConfigFile(byte[] hash){
        try {
            String policy = policyMap.get(Encoder.bytesToHex(hash)); //yeta kata miliraxaina

            File inputFile = new File(env.getProperty("pdp.config.path")); //path of pdp engine
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            dbFactory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
            dbFactory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(inputFile);

            XPath xPath = XPathFactory.newInstance().newXPath();
            Node policyLocationNode = (Node) xPath.compile("/pdp/policyProvider/policyLocation").evaluate(doc, XPathConstants.NODE);

            boolean checkForPolicy = (policyLocationNode.getTextContent().equals("policies/"+policy));
            if (!checkForPolicy){
                LOG.info("Updating PDP config file with policy : " + policy);
                setPolicy(inputFile, policy); // path of pdp engine with policy ko value in string
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*pdp config file ma default values hunxa now from above kun policy anusar PDP engine ready garne fix vayo so tyo policy ko values anusar
     PDP authenticate garna ready garyo. Now to authenticate mathi ko authenticate function call hunxa */

    /* private void setPolicy(File file, String policy) throws UnsupportedEncodingException {

        byte[] data = null;
        try (FileInputStream fis = new FileInputStream(file)) {
            data = new byte[(int) file.length()];
            fis.read(data);
        } catch (IOException e) {
            e.printStackTrace();
        }
        String input = new String(data, "UTF-8");
        String tag = "<policyLocation>";
        String closetag = "</policyLocation>";
        StringBuffer sbf = new StringBuffer(input);
        sbf.delete(input.indexOf(tag) + tag.length() , input.indexOf(closetag));
        input= sbf.toString();
        String newXML = input.substring(0, input.indexOf(tag) + tag.length()) + "policies/" + policy + input.substring(input.indexOf(tag) + tag.length(), input.length());
        try (FileWriter fw = new FileWriter(file)) {
            fw.write(newXML);
        } catch (IOException e) {
            e.printStackTrace();
        }
        LOG.info("PDP config file updated with policy : " + policy);
    }  */



