package tf.cyber.obdii.util;

public class ByteUtils {
    public static int[] extractBytes(String input) {
        String[] byteStrings = input.split(" ");

        int[] result = new int[byteStrings.length];

        for (int i = 0; i < result.length; i++) {
            result[i] = Integer.parseInt(byteStrings[i], 16);
        }

        return result;
    }

    public static Boolean[] byteToBooleanFourBits(int x) {
        if (x < 0 || x > 255) {
            throw new NumberFormatException("Value out of range!");
        }

        return byteToBooleanFourBits((byte) x);
    }

    public static Boolean[] byteToBooleanFourBits(byte x) {
        Boolean bool[] = new Boolean[4];

        bool[0] = ((x & 0x01) != 0);
        bool[1] = ((x & 0x02) != 0);
        bool[2] = ((x & 0x04) != 0);
        bool[3] = ((x & 0x08) != 0);

        return bool;
    }

    public static Boolean[] byteToBoolean(int x) {
        Boolean bool[] = new Boolean[8];

        bool[0] = ((x & 0x01) != 0);
        bool[1] = ((x & 0x02) != 0);
        bool[2] = ((x & 0x04) != 0);
        bool[3] = ((x & 0x08) != 0);
        bool[4] = ((x & 0x10) != 0);
        bool[5] = ((x & 0x20) != 0);
        bool[6] = ((x & 0x40) != 0);
        bool[7] = ((x & 0x80) != 0);

        return bool;
    }
}
