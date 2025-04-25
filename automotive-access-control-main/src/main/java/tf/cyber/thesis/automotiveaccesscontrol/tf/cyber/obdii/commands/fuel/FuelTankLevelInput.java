package tf.cyber.obdii.commands.fuel;

import tf.cyber.obdii.commands.OBD2Command;
import tf.cyber.obdii.util.ByteUtils;

public class FuelTankLevelInput extends OBD2Command<Double> {
    @Override
    public String command() {
        return "01 2F";
    }

    @Override
    public Double result() {
        int[] bytes = ByteUtils.extractBytes(rawData);
        return (100d / 255d) * bytes[bytes.length - 1];
    }

    @Override
    public String getFriendlyName() {
        return "Fuel Tank Level Input (%)";
    }

    @Override
    public String getKey() {
        return "fuel_tank_level_input";
    }
}
