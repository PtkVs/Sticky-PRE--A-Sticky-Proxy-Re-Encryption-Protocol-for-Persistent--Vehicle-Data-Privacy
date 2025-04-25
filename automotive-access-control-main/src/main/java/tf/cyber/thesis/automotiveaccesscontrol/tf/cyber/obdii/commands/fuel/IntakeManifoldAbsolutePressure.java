package tf.cyber.obdii.commands.fuel;

import tf.cyber.obdii.commands.OBD2Command;
import tf.cyber.obdii.util.ByteUtils;

public class IntakeManifoldAbsolutePressure extends OBD2Command<Integer> {
    @Override
    public String command() {
        return "01 0B";
    }

    @Override
    public Integer result() {
        int[] bytes = ByteUtils.extractBytes(rawData);
        return bytes[bytes.length - 1];
    }

    @Override
    public String getFriendlyName() {
        return "Intake manifold absolute pressure (kPa)";
    }

    @Override
    public String getKey() {
        return "intake_manifold_absolute_pressure";
    }
}
