package tf.cyber.obdii.commands.connection;

import tf.cyber.obdii.commands.OBD2Command;
import tf.cyber.obdii.commands.engine.CommandedExhaustGasRecirculation;
import tf.cyber.obdii.commands.engine.EvaporationSystemVaporPressure;
import tf.cyber.obdii.commands.engine.ExhaustGasRecirculationError;
import tf.cyber.obdii.commands.engine.*;
import tf.cyber.obdii.commands.fuel.*;
import tf.cyber.obdii.commands.vehicle.*;
import tf.cyber.obdii.util.ByteUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class SupportedPID21to40 extends OBD2Command<List<Class<OBD2Command<?>>>> {
    private static final Class<OBD2Command<?>>[][] PID_INDEX = new Class[][]{
            {DistanceTraveledWithMalfunctionIndicatorLamp.class, FuelRailPressure.class, FuelRailGaugePressure.class, OxygenSensor1Secondary.class},
            {OxygenSensor2Secondary.class, OxygenSensor3Secondary.class, OxygenSensor4Secondary.class, OxygenSensor5Secondary.class},
            {OxygenSensor6Secondary.class, OxygenSensor7Secondary.class, OxygenSensor8Secondary.class, CommandedExhaustGasRecirculation.class},
            {ExhaustGasRecirculationError.class, CommandedEvaporativePurge.class, FuelTankLevelInput.class, WarmUpsSinceCodesCleared.class},
            {DistanceTraveledSinceCodesCleared.class, EvaporationSystemVaporPressure.class, AbsoluteBarometricPressure.class, OxygenSensor1Tertiary.class},
            {OxygenSensor2Tertiary.class, OxygenSensor3Tertiary.class, OxygenSensor4Tertiary.class, OxygenSensor5Tertiary.class},
            {OxygenSensor6Tertiary.class, OxygenSensor7Tertiary.class, OxygenSensor8Tertiary.class, CatalystTemperatureBank1Sensor1.class},
            {CatalystTemperatureBank2Sensor1.class, CatalystTemperatureBank1Sensor2.class, CatalystTemperatureBank2Sensor2.class, SupportedPID41to60.class}
    };

    @Override
    public String command() {
        return "01 20";
    }

    @Override
    public List<Class<OBD2Command<?>>> result() {
        List<Class<OBD2Command<?>>> supportedCommands = new LinkedList<>();

        String[] bytesStr = rawData.split(" ");
        String str = bytesStr[bytesStr.length - 4] + bytesStr[bytesStr.length - 3]
                + bytesStr[bytesStr.length - 2] + bytesStr[bytesStr.length - 1];

        String[] chars = str.split("");

        for (int c = 0; c < chars.length; c++) {
            int b = Integer.parseInt(chars[c], 16);

            List<Boolean> mask = Arrays.asList(ByteUtils.byteToBooleanFourBits(b));
            Collections.reverse(mask);

            for (int i = 0; i < mask.size(); i++) {
                if (mask.get(i)) {
                    supportedCommands.add(PID_INDEX[c][i]);
                }
            }
        }

        return supportedCommands;
    }

    @Override
    public String getFriendlyName() {
        return "Fetch Supported PIDs (21 to 40)";
    }

    @Override
    public String getKey() {
        return "pid_support_21_40";
    }
}
