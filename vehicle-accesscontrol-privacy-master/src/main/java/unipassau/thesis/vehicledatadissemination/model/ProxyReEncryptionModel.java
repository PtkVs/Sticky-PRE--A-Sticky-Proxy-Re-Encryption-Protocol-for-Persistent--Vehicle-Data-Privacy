package unipassau.thesis.vehicledatadissemination.model;

import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.springframework.boot.SpringApplication;
import unipassau.thesis.vehicledatadissemination.VehicleDataDisseminationApplication;
import unipassau.thesis.vehicledatadissemination.util.OpenPRE;

import java.io.File;

public class ProxyReEncryptionModel {

    public enum SecurityLevel{
        SECURITY_LEVEL_128,
        SECURITY_LEVEL_192,
        SECURITY_LEVEL_256;
    }

    private String schemeName;
    private int plainTextModulus;
    private int ringSize;
    private SecurityLevel securityLevel;

    public ProxyReEncryptionModel(String schemeName, int plainTextModulus, int ringSize, SecurityLevel securityLevel) {
        this.schemeName = schemeName;
        this.plainTextModulus = plainTextModulus;
        this.ringSize = ringSize;
        this.securityLevel = securityLevel;
    }

    public String getSchemeName() {
        return schemeName;
    }

    public void setSchemeName(String schemeName) {
        this.schemeName = schemeName;
    }

    public int getPlainTextModulus() {
        return plainTextModulus;
    }

    public void setPlainTextModulus(int plainTextModulus) {
        this.plainTextModulus = plainTextModulus;
    }

    public int getRingSize() {
        return ringSize;
    }

    public void setRingSize(int ringSize) {
        this.ringSize = ringSize;
    }

    public SecurityLevel getSecurityLevel() {
        return securityLevel;
    }

    public void setSecurityLevel(SecurityLevel securityLevel) {
        this.securityLevel = securityLevel;
    }


}
