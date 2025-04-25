package tf.cyber.obdii.commands.vehicle;

import tf.cyber.obdii.commands.OBD2Command;
import tf.cyber.obdii.util.ByteUtils;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class MonitorStatusDriveCycle extends OBD2Command<Map<String, String>> {
    // Null values mark reserved positions.
    private final String[] commonTests = {"Misfire", "Fuel System", "Components"};
    private final String[] dieselTests = {"Catalyst", "Heated Catalyst", "Evaporative System", "Secondary Air System", "A/C Refrigerant", "Oxygen Sensor", "Oxygen Sensor Heater", "EGR System"};
    private final String[] ignitionTests = {"NMHC Catalyst", "NOx/SCR Monitor", null, "Boost Pressure", null, "Exhaust Gas Sensor", "PM filter monitoring", "EGR/VVT System"};

    @Override
    public String command() {
        return "01 41";
    }

    @Override
    public Map<String, String> result() {
        int[] bytes = ByteUtils.extractBytes(rawData);

        int a = bytes[bytes.length - 4];
        int b = bytes[bytes.length - 3];
        int c = bytes[bytes.length - 2];
        int d = bytes[bytes.length - 1];

        // Motor indicator light is encoded in first bit of A.
        boolean motorIndicatorOn = ((a & 128) != 0);

        // Number of confirmed emissions-related DTCs available.
        int confirmedDTC = a & 127;

        // Define monitor type (diesel, otto or wankel)
        int motorType = (b & 8) >> 3;

        Boolean[] bBytes = ByteUtils.byteToBoolean(b);
        Boolean[] cBytes = ByteUtils.byteToBoolean(c);
        Boolean[] dBytes = ByteUtils.byteToBoolean(d);

        List<String> availableTests = new LinkedList<>();
        List<String> incompleteTests = new LinkedList<>();

        // Common tests.
        for (int bb = 0; bb < bBytes.length - 1; bb++) {
            if (bb < 3 && bBytes[bb]) {
                // Available are encoded from B0 to B2.
                availableTests.add(commonTests[bb]);
            } else if (bb > 3 && bBytes[bb]) {
                // Incomplete are encoded from B4 to B6.
                incompleteTests.add(commonTests[(bb - 1) % 3]);
            }
        }

        String[] tests = null;

        // motorType 0 is otto/wankel, motorType 1 is diesel.
        if (motorType == 0) {
            tests = ignitionTests;
        } else if (motorType == 1) {
            tests = dieselTests;
        } else {
            throw new IllegalStateException("Unknown motor type received from vehicle!");
        }

        // C bytes encode available tests.
        for (int cb = 0; cb < cBytes.length; cb++) {
            if (cBytes[cb]) {
                availableTests.add(tests[cb]);
            }
        }

        // D bytes encode available tests.
        for (int db = 0; db < cBytes.length; db++) {
            if (dBytes[db]) {
                incompleteTests.add(tests[db]);
            }
        }

        return Map.of(
                "motorIndicatorLight", Boolean.toString(motorIndicatorOn),
                "confirmedDTC", Integer.toString(confirmedDTC),
                "availableTests", availableTests.toString(),
                "incompleteTests", incompleteTests.toString()
        );
    }

    @Override
    public String getFriendlyName() {
        return "Monitor status since DTCs cleared";
    }

    @Override
    public String getKey() {
        return "monitor_status_drive_cycle";
    }
}
