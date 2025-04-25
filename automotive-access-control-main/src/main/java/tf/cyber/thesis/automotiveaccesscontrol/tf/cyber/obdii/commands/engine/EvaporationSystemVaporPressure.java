package tf.cyber.obdii.commands.engine;

import tf.cyber.obdii.commands.OBD2Command;
import tf.cyber.obdii.util.ByteUtils;

public class EvaporationSystemVaporPressure extends OBD2Command<Double> {
    @Override
    public String command() {
        return "01 32";
    }

    @Override
    public Double result() {
        int[] bytes = ByteUtils.extractBytes(rawData);

        // TODO: a und b sind zweierkomplement!
        int a = bytes[bytes.length - 2];
        int b = bytes[bytes.length - 1];

        return (256d * a + b) / 4d;
    }

    @Override
    public String getFriendlyName() {
        return "Evaporation System Vapor Pressure (Pa)";
    }

    @Override
    public String getKey() {
        return "evaporation_system_vapor_pressure";
    }
}
