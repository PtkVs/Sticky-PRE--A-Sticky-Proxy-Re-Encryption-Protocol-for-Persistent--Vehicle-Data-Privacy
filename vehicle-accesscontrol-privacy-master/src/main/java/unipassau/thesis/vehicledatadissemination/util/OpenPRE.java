package unipassau.thesis.vehicledatadissemination.util;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.Platform;

public interface OpenPRE extends Library {
    // Load the C++ OpenPRE library from the system
    OpenPRE INSTANCE = (OpenPRE)
            Native.load((Platform.isLinux() ? "crypto" : "c"),
                    OpenPRE.class);
    // Generate the crypto context for our PRE scheme
    void cryptoContextGen(String schemeName,
                          String cryptoFolder,
                          String filename,
                          int plaintextModulus,
                          int ringDimension,
                          String securityLevel);
    // Generate the key pairs
    void keysGen(String cryptoContext, String destinationPath);
    // Encrypt the data
    void  encrypt(String publickey, String plaintext, String destinationPath);
    // Decrypt the data
    String decrypt(String secretKey, String ciphertext);
    // Generate the re-encryption keys
    void reKeyGen(String secretKey, String publicKey, String destinationPath);
    // Re-encrypt the data
    void reEncrypt(String ciphertext, String reEncryptionKey, String destinationPath);
}
