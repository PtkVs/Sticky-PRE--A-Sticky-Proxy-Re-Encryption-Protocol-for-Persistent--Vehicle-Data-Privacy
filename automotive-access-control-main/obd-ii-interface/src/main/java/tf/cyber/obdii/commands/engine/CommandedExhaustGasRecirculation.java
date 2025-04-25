package tf.cyber.obdii.commands.engine;

import tf.cyber.obdii.commands.OBD2Command;
import tf.cyber.obdii.util.ByteUtils;

public class CommandedExhaustGasRecirculation extends OBD2Command<Double> {
    @Override
    public String command() {
        return "01 2C";
    }

    @Override
    public Double result() {
        int[] bytes = ByteUtils.extractBytes(rawData);
        return (100d/255d) / bytes[bytes.length - 1];
    }

    @Override
    public String getFriendlyName() {
        return "Commanded EGR (%)";
    }

    @Override
    public String getKey() {
        return "commanded_exhaust_gas_recirculation";
    }
}
