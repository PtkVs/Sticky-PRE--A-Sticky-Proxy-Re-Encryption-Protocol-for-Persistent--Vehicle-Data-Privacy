package tf.cyber.obdii.commands.engine;

import tf.cyber.obdii.commands.OBD2Command;
import tf.cyber.obdii.util.ByteUtils;

public class MassAirFlowRate extends OBD2Command<Double> {
    @Override
    public String command() {
        return "01 10";
    }

    @Override
    public Double result() {
        int[] bytes = ByteUtils.extractBytes(rawData);

        int a = bytes[bytes.length - 2];
        int b = bytes[bytes.length - 1];

        return (256d * a + b) / 100d;
    }

    @Override
    public String getFriendlyName() {
        return "Mass air flow sensor (MAF) air flow rate (grams/sec)";
    }

    @Override
    public String getKey() {
        return "mass_air_flow_rate";
    }


}
