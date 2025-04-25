package tf.cyber.obdii.commands.fuel;

import tf.cyber.obdii.commands.OBD2Command;
import tf.cyber.obdii.util.ByteUtils;

import java.util.Arrays;

public class FuelSystemStatus extends OBD2Command<FuelSystemStatus.FuelSystemStatusType> {
    @Override
    public String command() {
        return "01 03";
    }

    @Override
    public FuelSystemStatusType result() {
        int[] bytes = ByteUtils.extractBytes(rawData);
        return FuelSystemStatusType.fromResponse(bytes[bytes.length - 1]);
    }

    @Override
    public String getFriendlyName() {
        return "Fuel System Status";
    }

    @Override
    public String getKey() {
        return "fuel_system_status";
    }

    public enum FuelSystemStatusType {
        MOTOR_OFF(0),
        OPEN_LOOP_DUE_TO_INSUFFICIENT_ENGINE_TEMP(1),
        CLOSED_LOOP_USING_OXYGEN_SENSOR(2),
        OPEN_LOOP_DUE_TO_ENGINE_LOAD(4),
        OPEN_LOOP_DUE_TO_SYSTEM_FAILURE(8),
        CLOSED_LOOP_USING_OXYGEN_SENSOR_BUT_FAULT_IN_FEEDBACK_SYSTEM(16);

        final int value;

        FuelSystemStatusType(int value) {
            this.value = value;
        }

        static FuelSystemStatusType fromResponse(int status) {
            return Arrays.stream(FuelSystemStatusType.values())
                    .filter(val -> val.value == status)
                    .findFirst()
                    .orElseThrow(IllegalArgumentException::new);
        }
    }
}
