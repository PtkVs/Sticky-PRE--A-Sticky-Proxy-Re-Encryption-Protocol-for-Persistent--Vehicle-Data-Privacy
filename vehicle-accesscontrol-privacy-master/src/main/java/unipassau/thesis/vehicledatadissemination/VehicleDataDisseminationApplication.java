package unipassau.thesis.vehicledatadissemination;

import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import unipassau.thesis.vehicledatadissemination.model.ProxyReEncryptionModel;
import unipassau.thesis.vehicledatadissemination.util.OpenPRE;

import java.io.File;


@SpringBootApplication
public class VehicleDataDisseminationApplication implements CommandLineRunner {

	private static Logger LOG = LoggerFactory
			.getLogger(VehicleDataDisseminationApplication.class);

	@Value("${app.cryptoFolder}")
	private String cryptoFolder;
	@Value("${app.policyFolder}")
	private String policyFolder;
	@Value("${pre.schemeName}")
	private String schemeName;
	@Value("${pre.plainTextModulus}")
	private int plainTextModulus;
	@Value("${pre.ringSize}")
	private int ringSize;
	@Value("${pre.securityLevel}")
	private ProxyReEncryptionModel.SecurityLevel securityLevel;
	@Value("${app.tmpFolder}")
	private String tmpFolder;
	@Value("${app.reEncKeysFolder}")
	private String reEncKeysFolder;


	public static void main(String[] args) {
		SpringApplication.run(VehicleDataDisseminationApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		File temporaryFolder= new File(tmpFolder);
		FileUtils.cleanDirectory(temporaryFolder);
		ProxyReEncryptionModel pre = new ProxyReEncryptionModel(schemeName, plainTextModulus, ringSize, securityLevel);


		LOG.info("EXECUTING : Generating crypto context");
		OpenPRE.INSTANCE.cryptoContextGen(pre.getSchemeName(),
				cryptoFolder,
				"cryptocontext",
				256,
				pre.getRingSize(),
				pre.getSecurityLevel().toString());
		LOG.info("EXECUTING : Generating key pairs for Alice, Bob and Carlos");
		OpenPRE.INSTANCE.keysGen(cryptoFolder + "cryptocontext", cryptoFolder + "alice");
		OpenPRE.INSTANCE.keysGen(cryptoFolder + "cryptocontext", cryptoFolder + "bob");
		OpenPRE.INSTANCE.keysGen(cryptoFolder + "cryptocontext", cryptoFolder + "carlos");
		LOG.info("EXECUTING : Generating re encryption keys from Alice to Bob");
		OpenPRE.INSTANCE.reKeyGen(cryptoFolder + "alice-private-key",
				cryptoFolder + "bob-public-key",
				reEncKeysFolder + "alice2bob" );
		LOG.info("EXECUTING : Generating re encryption keys from Alice to Carlos");
		OpenPRE.INSTANCE.reKeyGen(cryptoFolder + "carlos-private-key",
				cryptoFolder + "carlos-public-key",
				reEncKeysFolder + "alice2carlos" );
	}

}
