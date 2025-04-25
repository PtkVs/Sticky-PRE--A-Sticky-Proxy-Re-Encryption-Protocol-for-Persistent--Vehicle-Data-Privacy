package tf.cyber.thesis.automotiveaccesscontrol.util;

import org.apache.commons.lang3.RandomStringUtils;

public class PREUtils {

    private String cryptoFolder;
    private String schemeName ;
    private String ccPath;
    private int plainttextModulus;
    private int multiplicativeDepth;

    public PREUtils(String schemeName, int plainttextModulus, int multiplicativeDepth) {
        this.schemeName = schemeName;
        this.plainttextModulus = plainttextModulus;
        this.multiplicativeDepth = multiplicativeDepth;
    }

    public String getCryptoFolder() {
        return cryptoFolder;
    }

    public void setCryptoFolder(String cryptoFolder) {
        this.cryptoFolder = cryptoFolder;
    }

    public String getSchemeName() {
        return schemeName;
    }

    public void setSchemeName(String schemeName) {
        this.schemeName = schemeName;
    }

    public String getCcPath() {
        return ccPath;
    }

    public void setCcPath(String ccPath) {
        this.ccPath = ccPath;
    }

    public int getPlainttextModulus() {
        return plainttextModulus;
    }

    public void setPlainttextModulus(int plainttextModulus) {
        this.plainttextModulus = plainttextModulus;
    }

    public int getMultiplicativeDepth() {
        return multiplicativeDepth;
    }

    public void setMultiplicativeDepth(int multiplicativeDepth) {
        this.multiplicativeDepth = multiplicativeDepth;
    }


    
    public static void main(String[] args) {

        PREUtils pre = new PREUtils("BFV", 256, 2);
        pre.setCryptoFolder("/home/somey/IdeaProjects/automotive-access-control/src/main/resources/crypto/");

        //Random String generator
        String generatedString = RandomStringUtils.randomAlphanumeric(300);
        OpenPRE.INSTANCE.cryptoContextGen(pre.getSchemeName(), pre.getCryptoFolder(),"cryptocontext1.json", pre.getPlainttextModulus(),  pre.getMultiplicativeDepth() );
        OpenPRE.INSTANCE.keysGen(pre.getCryptoFolder() , "cryptocontext1.json", "a");
        String a  = OpenPRE.INSTANCE.Encrypt( "a-public-key.json", generatedString, pre.getCryptoFolder(),  "cryptocontext1.json");
        System.out.println(a.length());
        String b = OpenPRE.INSTANCE.Decrypt("a-private-key.json", a, pre.getCryptoFolder(),  "cryptocontext1.json");
        System.out.print("Plaintext decrypted is : "+b);
        OpenPRE.INSTANCE.keysGen(pre.getCryptoFolder()  , "cryptocontext1.json", "b");
        OpenPRE.INSTANCE.ReKeyGen( "a-private-key.json", "b-public-key.json", pre.getCryptoFolder() , "cryptocontext1.json", "a2b");
        String c = OpenPRE.INSTANCE.ReEncrypt(a, "a2b-re-enc-key.json", pre.getCryptoFolder(), "cryptocontext1.json");
        String d = OpenPRE.INSTANCE.Decrypt( "b-private-key.json", c , pre.getCryptoFolder(),  "cryptocontext1.json");
        System.out.print("Decryption of Ciphertext re encrypted : "+d);
    }
}