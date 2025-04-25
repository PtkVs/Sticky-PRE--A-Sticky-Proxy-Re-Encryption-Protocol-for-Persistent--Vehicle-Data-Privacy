package tf.cyber.thesis.automotiveaccesscontrol.util;

import com.sun.jna.Library;

public interface OpenPRE extends Library {
    OpenPRE INSTANCE = new OpenPRE() {
        @Override
        public void cryptoContextGen(String schemeName, String cryptoFolder, String filename, int plaintextModulus, int multiplicativeDepth) {

        }

        @Override
        public void keysGen(String cryptoFolder, String cryptoContext, String filename) {

        }

        @Override
        public String Encrypt(String publickey, String plaintext, String cryptoFolder, String cryptoContext) {
            return null;
        }

        @Override
        public String Decrypt(String secretKey, String ciphertext, String cryptoFolder, String cryptoContext) {
            return null;
        }

        @Override
        public void ReKeyGen(String secretKey, String publicKey, String cryptoFolder, String cryptoContext, String filename) {

        }

        @Override
        public String ReEncrypt(String ciphertext, String reEncryptionKey, String cryptoFolder, String cryptoContext) {
            return null;
        }
    };

    void cryptoContextGen(String schemeName, String cryptoFolder, String s, int plainttextModulus, int multiplicativeDepth);

    void keysGen(String cryptoFolder, String cryptoContext, String filename);

    String Encrypt(String s, String string, String cryptoFolder, String s1);

    String Decrypt(String s, String a, String cryptoFolder, String s1);

    void ReKeyGen(String s, String s1, String cryptoFolder, String s2, String a2b);

    String ReEncrypt(String a, String s, String cryptoFolder, String s1);
}
