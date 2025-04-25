package unipassau.thesis.vehicledatadissemination.services;

import org.ow2.authzforce.core.pdp.impl.BasePdpEngine;
import org.ow2.authzforce.core.pdp.impl.PdpEngineConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import unipassau.thesis.vehicledatadissemination.pep.EmbeddedPdpInterceptor;
import unipassau.thesis.vehicledatadissemination.util.Encoder;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;
import java.io.*;
import java.security.Principal;
import java.util.HashMap;

@Service
public class PolicyEnforcementService {
    Logger LOG = LoggerFactory.getLogger(PolicyEnforcementService.class);

    @Autowired
    private HashMap<String, String> policyMap;

    @Autowired
    org.springframework.core.env.Environment env;

    private PdpEngineConfiguration pdpEngineConfiguration;

    public boolean authorize(Principal principal, String requestURI, String action) throws Exception {
        // Set the pdp engine configuration with the PDP config file
        pdpEngineConfiguration = PdpEngineConfiguration.getInstance(env.getProperty("pdp.config.path"));
        // Deploy an Embedded PDP engine
        final BasePdpEngine pdp = new BasePdpEngine(pdpEngineConfiguration);
        // Pass the pdp engine to a pdpInterceptor to create an evaluate an XACML request
        final EmbeddedPdpInterceptor pdpInterceptor = new EmbeddedPdpInterceptor(pdp);
        // evaluate the XACML request against the policy
        boolean res = pdpInterceptor.authorize(principal, requestURI, action);

        return res;
    }


    private void setPolicy(File file, String policy) throws UnsupportedEncodingException {

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
    }

    public void setPdpConfigFile(byte[] hash){
        try {
            String policy = policyMap.get(Encoder.bytesToHex(hash));

            File inputFile = new File(env.getProperty("pdp.config.path"));
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
                setPolicy(inputFile, policy);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}

/*
package unipassau.thesis.vehicledatadissemination.services;

import org.ow2.authzforce.core.pdp.impl.BasePdpEngine;
import org.ow2.authzforce.core.pdp.impl.PdpEngineConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import unipassau.thesis.vehicledatadissemination.pep.EmbeddedPdpInterceptor;
import unipassau.thesis.vehicledatadissemination.util.Encoder;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;
import java.io.*;
import java.security.Principal;
import java.util.HashMap;

@Service
public class PolicyEnforcementService {
    Logger LOG = LoggerFactory.getLogger(PolicyEnforcementService.class);

    @Autowired
    private HashMap<String, String> policyMap;

    @Autowired
    org.springframework.core.env.Environment env;

    private PdpEngineConfiguration pdpEngineConfiguration;

        //aba PDP engine ready vayo to authenticate here requested values anusar PDP engine (jun varkhar ready vayo according to below steps) le authenticate garxa and gives decision
    public boolean authorize(Principal principal, String requestURI, String action) throws Exception {
        // Set the pdp engine configuration with the PDP config file
        pdpEngineConfiguration = PdpEngineConfiguration.getInstance(env.getProperty("pdp.config.path"));
        // Deploy an Embedded PDP engine
        final BasePdpEngine pdp = new BasePdpEngine(pdpEngineConfiguration);
        // Pass the pdp engine to a pdpInterceptor to create an evaluate an XACML request
        final EmbeddedPdpInterceptor pdpInterceptor = new EmbeddedPdpInterceptor(pdp);
        // evaluate the XACML request against the policy
        boolean res = pdpInterceptor.authorize(principal, requestURI, action);

        return res;
    }


    //pdp lai kun policy ko hash ho tyo policy ko name anusar update and check tyo policy xa ki nai xa then next step policy anusar update
    public void setPdpConfigFile(byte[] hash){
        try {
            String policy = policyMap.get(Encoder.bytesToHex(hash));

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
  /*  private void setPolicy(File file, String policy) throws UnsupportedEncodingException {

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
    }

}
*/