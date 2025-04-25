package tf.cyber.obdii.commands.engine;

import tf.cyber.obdii.commands.OBD2Command;
import tf.cyber.obdii.util.ByteUtils;

public class IntakeAirTemperature extends OBD2Command<Integer> {
    @Override
    public String command() {
        return "01 0F";
    }

    @Override
    public Integer result() {
        int[] bytes = ByteUtils.extractBytes(rawData);
        return bytes[bytes.length - 1] - 40;
    }

    @Override
    public String getFriendlyName() {
        return "Intake Air Temperature (Celsius)";
    }

    @Override
    public String getKey() {
        return "intake_air_temperature";
    }
}
