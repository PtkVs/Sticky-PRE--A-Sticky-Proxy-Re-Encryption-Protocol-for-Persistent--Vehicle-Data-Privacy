package tf.cyber.obdii.commands.engine;

import tf.cyber.obdii.commands.OBD2Command;
import tf.cyber.obdii.util.ByteUtils;

public class EngineOilTemperature extends OBD2Command<Integer> {
    @Override
    public String command() {
        return "01 5C";
    }

    @Override
    public Integer result() {
        int[] bytes = ByteUtils.extractBytes(rawData);
        return bytes[bytes.length - 1] - 40;
    }

    @Override
    public String getFriendlyName() {
        return "Engine Oil Temperature (Celsius)";
    }

    @Override
    public String getKey() {
        return "engine_oil_temperature";
    }
}
