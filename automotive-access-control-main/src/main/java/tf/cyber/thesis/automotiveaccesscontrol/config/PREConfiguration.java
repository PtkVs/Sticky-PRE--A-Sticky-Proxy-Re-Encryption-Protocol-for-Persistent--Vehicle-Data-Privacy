package tf.cyber.thesis.automotiveaccesscontrol.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "pre")
@ConstructorBinding
public class PREConfiguration {

    private String schemeName;
    private int plainttextModulus;
    private int multiplicativeDepth;
    private String cryptoFolder;


    public String getSchemeName() {
        return schemeName;
    }

    public void setSchemeName(String schemeName) {
        this.schemeName = schemeName;
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

    public String getCryptoFolder() {
        return cryptoFolder;
    }

    public void setCryptoFolder(String cryptoFolder) {
        this.cryptoFolder = cryptoFolder;
    }
}
