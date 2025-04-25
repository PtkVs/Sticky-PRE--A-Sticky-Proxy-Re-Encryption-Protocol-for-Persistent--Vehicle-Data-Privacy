package tf.cyber.authzforce.time.datatypes.java;

import java.time.DayOfWeek;
import java.util.Objects;
import java.util.SimpleTimeZone;

public class DayOfWeekType {
    private DayOfWeek dayOfWeek = DayOfWeek.MONDAY;
    private Integer hours;
    private Integer minutes;
    private boolean positiveShift;

    public DayOfWeekType() {
    }

    public DayOfWeek getDayOfWeek() {
        return dayOfWeek;
    }

    public void setDayOfWeek(DayOfWeek dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    public int getHours() {
        return hours;
    }

    public void setHours(int hours) {
        this.hours = hours;
    }

    public int getMinutes() {
        return minutes;
    }

    public void setMinutes(int minutes) {
        this.minutes = minutes;
    }

    public boolean isPositiveShift() {
        return positiveShift;
    }

    public void setPositiveShift(boolean positiveShift) {
        this.positiveShift = positiveShift;
    }

    public SimpleTimeZone getTimezone() {
        if (hours != null && minutes != null) {
            int offset = hours * 60 * 60000 + minutes * 60000;
            offset = positiveShift ? offset : -1 * offset;
            return new SimpleTimeZone(offset, null);
        } else {
            throw new IllegalArgumentException("Cannot provide timezone information if not set!");
        }
    }

    public boolean hasTimezone() {
        return hours != null && minutes != null;
    }

    @Override
    public String toString() {
        return "DayOfWeekType{" +
                "dayOfWeek=" + dayOfWeek +
                ", hours=" + String.format("%02d", hours) +
                ", minutes=" + String.format("%02d", minutes) +
                ", positiveShift=" + positiveShift +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DayOfWeekType that = (DayOfWeekType) o;
        return hours == that.hours && minutes == that.minutes && positiveShift == that.positiveShift && dayOfWeek == that.dayOfWeek;
    }

    @Override
    public int hashCode() {
        return Objects.hash(dayOfWeek, hours, minutes, positiveShift);
    }
}
