package tf.cyber.obdii.commands.engine;

import tf.cyber.obdii.commands.OBD2Command;
import tf.cyber.obdii.util.ByteUtils;

import java.util.Arrays;

public class CommandedSecondaryAirStatus extends OBD2Command<CommandedSecondaryAirStatus.CommandedSecondaryAirStatusType> {
    @Override
    public String command() {
        return "01 12";
    }

    @Override
    public CommandedSecondaryAirStatusType result() {
        int[] bytes = ByteUtils.extractBytes(rawData);
        return CommandedSecondaryAirStatusType.fromResponse(bytes[bytes.length - 1]);
    }

    @Override
    public String getFriendlyName() {
        return "Commanded secondary air status";
    }

    @Override
    public String getKey() {
        return "commanded_secondary_air_status";
    }

    public enum CommandedSecondaryAirStatusType {
        UPSTREAM(1),
        DOWNSTREAM_OF_CATALYTIC_CONVERTER(2),
        FROM_OUTSIDE_OF_ATMOSPHERE_OR_OFF(4),
        PUMP_COMMANDED_ON_FOR_DIAGNOSTICS(8);

        final int value;

        CommandedSecondaryAirStatusType(int value) {
            this.value = value;
        }

        static CommandedSecondaryAirStatus.CommandedSecondaryAirStatusType fromResponse(int status) {
            return Arrays.stream(CommandedSecondaryAirStatus.CommandedSecondaryAirStatusType.values())
                    .filter(val -> val.value == status)
                    .findFirst()
                    .orElseThrow(IllegalArgumentException::new);
        }
    }
}
