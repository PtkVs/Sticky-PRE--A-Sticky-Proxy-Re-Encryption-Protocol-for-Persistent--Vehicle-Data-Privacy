package tf.cyber.obdii.commands.fuel;

import tf.cyber.obdii.commands.OBD2Command;
import tf.cyber.obdii.util.ByteUtils;

public class EthanolFuelLevel extends OBD2Command<Double> {
    @Override
    public String command() {
        return "01 52";
    }

    @Override
    public Double result() {
        int[] bytes = ByteUtils.extractBytes(rawData);
        int a = bytes[bytes.length - 1];

        return (100 / 255d) * a;
    }

    @Override
    public String getFriendlyName() {
        return "Ethanol Fuel (%)";
    }

    @Override
    public String getKey() {
        return "ethanol_fuel";
    }
}
