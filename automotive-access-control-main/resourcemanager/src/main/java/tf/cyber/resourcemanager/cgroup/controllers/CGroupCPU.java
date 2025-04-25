package tf.cyber.resourcemanager.cgroup.controllers;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;

public class CGroupCPU {
    public static final String CPU_MAX = "cpu.max";
    public static final String CPU_STAT = "cpu.stat";
    public static final String CPU_WEIGHT = "cpu.weight";
    public static final String CPU_WEIGHT_NICE = "cpu.weight.nice";

    private final Path cgroupPath;

    public CGroupCPU(Path cgroupPath) throws IOException {
        this.cgroupPath = cgroupPath;

        if (!Files.exists(Path.of(cgroupPath.toString(), "cgroup.controllers"))) {
            throw new IllegalArgumentException("cgroup.controllers file not found. Check your" +
                                                       "cgroup configuration.");
        }

        String availableControllers = Files.readString(Path.of(cgroupPath.toString(), "cgroup.controllers"));

        long controllerAvailable = Arrays.stream(availableControllers.split(" "))
                .map(String::strip)
                .filter(controller -> controller.equals("cpu"))
                .count();

        if (controllerAvailable != 1) {
            throw new IllegalArgumentException("cpu controller not found in cgroup.controllers file." +
                                                       "Check your cgroup configuration.");
        }
    }

    public String getCpuMax() throws IOException {
        return Files.readString(Paths.get(cgroupPath.toString(), CPU_MAX));
    }

    public String getCpuStat() throws IOException {
        return Files.readString(Paths.get(cgroupPath.toString(), CPU_STAT));
    }

    public String getCpuWeight() throws IOException {
        return Files.readString(Paths.get(cgroupPath.toString(), CPU_WEIGHT));
    }

    public String getCpuWeightNice() throws IOException {
        return Files.readString(Paths.get(cgroupPath.toString(), CPU_WEIGHT_NICE));
    }

    public void setCpuNice(int niceValue) throws IOException {
        if (niceValue < -20 || niceValue > 19) {
            throw new IllegalArgumentException("Nice value must be between -20 and +19.");
        }

        Files.writeString(Paths.get(cgroupPath.toString(), CPU_WEIGHT_NICE),
                          Integer.toString(niceValue),
                          StandardOpenOption.WRITE);
    }

    public void setCpuMax(int time, int fraction) throws IOException {
        if (time < 0 || fraction < 0) {
            throw new IllegalArgumentException("Time and fraction must not be negative.");
        }

        Files.writeString(Paths.get(cgroupPath.toString(), CPU_MAX),
                          time + " " + fraction,
                          StandardOpenOption.WRITE);
    }

    public void setCPUMaxUnlimited() throws IOException {
        System.out.println(Paths.get(cgroupPath.toString(), CPU_MAX));
        Files.writeString(Paths.get(cgroupPath.toString(), CPU_MAX),
                          "max 100000",
                          StandardOpenOption.WRITE);
    }
}
