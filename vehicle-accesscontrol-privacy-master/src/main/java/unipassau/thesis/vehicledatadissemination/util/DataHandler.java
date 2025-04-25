package unipassau.thesis.vehicledatadissemination.util;

import java.io.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class DataHandler {

    private static String hashFolder = System.getProperty("user.dir") + "/hsh/";

    public static void writer(String xmlPolicyPath, String ciphertext, int count) {
        byte[] ct = null;
        try {
            FileInputStream read = new FileInputStream(new File(ciphertext));
            ct = read.readAllBytes();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try (var out = new DataOutputStream(new BufferedOutputStream(
                new FileOutputStream(ciphertext)))) {

            //saving hash seperately
            byte[] hash = Encoder.xmlToHash(xmlPolicyPath); //policy path ko count anusar policy ko name k ho and tyo .xml typ ho vane tyo xml file ko sab kura saved as a string jaslai maile converted to xml
            out.write(hash);

            //  out.write(Encoder.xmlToHash(xmlPolicyPath));
        //    System.out.println("hash is :  " + Encoder.bytesToHex(Encoder.xmlToHash(xmlPolicyPath)));

            //dynamically creating the hash file name based on count
            String hashFile = hashFolder + count + ".bin";


            saveHashToFile(hashFile, hash);
            out.write(ct);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void saveHashToFile(String hashF, byte[] hashV) {
        try (FileOutputStream fos = new FileOutputStream(hashF)) {  //yo constructor ma jaba hashF ko value pass hunxa file create hune kam hunxa
            fos.write(hashV);
            //fos.write((Encoder.bytesToHex(hashV)).getBytes());  readable UTF-8 character format ma dinthyo
          //  System.out.println("Hash Saved in the following path as" + hashF);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static Map<String, byte[]> readOnlyHash(byte[] stickydata) throws NoSuchAlgorithmException { //DataAccessController bata stickydocument byte array object ayo which contains birnary data now tesabta hash ra data xutaune kam hunxa ra return garinxa
        Map<String, byte[]> stickydocument = new HashMap<>();
        byte[] hash = Arrays.copyOfRange(stickydata, 0, MessageDigest.getInstance(Encoder.hashAlgorithm).getDigestLength());
        stickydocument.put("hash", hash);
        return stickydocument; //DataAccessController lai hash ra data dutai send garyo


    }

    public static Map<String, byte[]> readOnlyData(byte[] stickydata) throws NoSuchAlgorithmException { //DataAccessController bata stickydocument byte array object ayo which contains birnary data now tesabta hash ra data xutaune kam hunxa ra return garinxa
        Map<String, byte[]> stickydocument = new HashMap<>();
        byte[] data = Arrays.copyOfRange(stickydata, MessageDigest.getInstance(Encoder.hashAlgorithm).getDigestLength(), stickydata.length);
        stickydocument.put("data", data);
        return stickydocument;

    }


}
