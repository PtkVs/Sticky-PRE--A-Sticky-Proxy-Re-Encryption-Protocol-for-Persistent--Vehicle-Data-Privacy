package tf.cyber.thesis.automotiveaccesscontrol.config;

import jssc.SerialPortException;
import jssc.SerialPortList;
import jssc.SerialPort;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.Scope;
import org.springframework.core.env.Environment;
import tf.cyber.obdii.commands.OBD2Command;
import tf.cyber.obdii.commands.connection.DisableEcho;
import tf.cyber.obdii.commands.connection.DisableHeader;
import tf.cyber.obdii.commands.connection.ResetELM;
import tf.cyber.obdii.io.OBD2Connection;

@Configuration
public class  OBD2Adapter {
    @Autowired
    private Environment env;

    private final Logger adapterLogger = LoggerFactory.getLogger("OBD2Adapter");

    @Bean
    @Profile("production")
    @Scope("singleton")
    public OBD2Connection obd2Connection() throws SerialPortException, InterruptedException {
        OBD2Connection conn = new OBD2Connection(env.getProperty("obd.device"));

        // Set initial properties on connection.
        ResetELM reset = new ResetELM();
        reset.execute(conn);

        DisableHeader disableHeader = new DisableHeader();
        disableHeader.execute(conn);

        DisableEcho disableEcho = new DisableEcho();
        disableEcho.execute(conn);

        return conn;
    }

    @Bean
    @Profile("debug")
    @Scope("singleton")
    public OBD2Connection obd2ConnectionDev() throws SerialPortException, InterruptedException {
        return new OBD2Connection() {

            @Override
            public synchronized String read() throws SerialPortException, InterruptedException {
                adapterLogger.info("Returning OBD2 dummy data.");
                return "FF FF FF FF";
            }

            @Override
            public synchronized void write(OBD2Command<?> command) throws SerialPortException {
                // do nothing.
                adapterLogger.info(command.getClass().getName() + " written to OBD2 connection.");
            }

            @Override
            public void close() throws SerialPortException {
                // do nothing.
                adapterLogger.info("OBD2 connection has been closed.");
            }
        };
    }
}
