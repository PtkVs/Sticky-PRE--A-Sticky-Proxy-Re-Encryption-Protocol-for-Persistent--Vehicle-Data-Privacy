package tf.cyber.obdii.commands.engine;

import tf.cyber.obdii.commands.OBD2Command;
import tf.cyber.obdii.util.ByteUtils;

public class CatalystTemperatureBank2Sensor1 extends OBD2Command<Double> {
    @Override
    public String command() {
        return "01 3D";
    }

    @Override
    public Double result() {
        int[] bytes = ByteUtils.extractBytes(rawData);

        int a = bytes[bytes.length - 2];
        int b = bytes[bytes.length - 1];

        return ((256 * a + b) / 10d) - 40;
    }

    @Override
    public String getFriendlyName() {
        return "Catalyst Temperature: Bank 2, Sensor 1 (Celsius)";
    }

    @Override
    public String getKey() {
        return "catalyst_temp_bank2_sensor1";
    }
}
