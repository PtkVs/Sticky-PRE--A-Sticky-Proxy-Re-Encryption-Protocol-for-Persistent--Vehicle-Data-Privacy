package tf.cyber.obdii.commands.vehicle;

import tf.cyber.obdii.commands.OBD2Command;
import tf.cyber.obdii.util.ByteUtils;

public class HybridBatteryPackRemainingLife extends OBD2Command<Double> {
    @Override
    public String command() {
        return "01 5B";
    }

    @Override
    public Double result() {
        int[] bytes = ByteUtils.extractBytes(rawData);
        int a = bytes[bytes.length - 1];
        return (100 / 255d) * a;
    }

    @Override
    public String getFriendlyName() {
        return "Hybrid battery pack remaining life (%)";
    }

    @Override
    public String getKey() {
        return "hybrid_battery_remaining_life";
    }
}
