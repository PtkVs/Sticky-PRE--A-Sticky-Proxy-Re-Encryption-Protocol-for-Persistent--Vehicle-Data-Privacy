package tf.cyber.obdii.commands.engine;

import tf.cyber.obdii.commands.OBD2Command;
import tf.cyber.obdii.util.ByteUtils;

public class EvaporationSystemVaporPressure2 extends OBD2Command<Integer> {
    @Override
    public String command() {
        return "01 54";
    }

    @Override
    public Integer result() {
        int[] bytes = ByteUtils.extractBytes(rawData);

        // TODO: a und b sind zweierkomplement!
        int a = bytes[bytes.length - 2];
        int b = bytes[bytes.length - 1];

        return (256 * a) + b;
    }

    @Override
    public String getFriendlyName() {
        return "Evaporation System Vapor Pressure (Pa)";
    }

    @Override
    public String getKey() {
        return "evaporation_system_vapor_pressure2";
    }
}
