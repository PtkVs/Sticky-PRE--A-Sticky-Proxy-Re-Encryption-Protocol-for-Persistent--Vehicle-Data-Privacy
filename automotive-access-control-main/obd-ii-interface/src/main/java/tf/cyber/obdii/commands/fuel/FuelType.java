package tf.cyber.obdii.commands.fuel;

import tf.cyber.obdii.commands.OBD2Command;
import tf.cyber.obdii.commands.engine.CommandedSecondaryAirStatus;
import tf.cyber.obdii.util.ByteUtils;

import java.util.Arrays;

public class FuelType extends OBD2Command<FuelType.FuelTypeValue> {
    @Override
    public String command() {
        return "01 51";
    }

    @Override
    public FuelTypeValue result() {
        int[] bytes = ByteUtils.extractBytes(rawData);
        int a = bytes[bytes.length - 1];
        return FuelTypeValue.fromResponse(a);
    }

    @Override
    public String getFriendlyName() {
        return "Fuel Type";
    }

    @Override
    public String getKey() {
        return "fuel_type";
    }

    public enum FuelTypeValue {
        NOT_AVAILABLE(0),
        GASOLINE(1),
        METHANOL(2),
        ETHANOL(3),
        DIESEL(4),
        LPG(5),
        CNG(6),
        PROPANE(7),
        ELECTRIC(8),
        BIFUEL_GASOLINE(9),
        BIFUEL_METHANOL(10),
        BIFUEL_ETHANOL(11),
        BIFUEL_LPG(12),
        BIFUEL_CNG(13),
        BIFUEL_PROPANE(14),
        BIFUEL_ELECTRICITY(15),
        BIFUEL_ELECTRIC_COMBUSTION(16),
        HYBRID_GASOLINE(17),
        HYBRID_ETHANOL(18),
        HYBRID_DIESEL(19),
        HYBRID_ELECTRIC(20),
        HYBRID_ELECTRIC_COMBUSTION(21),
        HYBRID_REGENERATIVE(22),
        BIFUEL_DIESEL(23);

        int value;

        FuelTypeValue(int value) {
            this.value = value;
        }

        static FuelType.FuelTypeValue fromResponse(int status) {
            return Arrays.stream(FuelType.FuelTypeValue.values())
                    .filter(val -> val.value == status)
                    .findFirst()
                    .orElseThrow(IllegalArgumentException::new);
        }
    }
}
