package tf.cyber.resourcemanager.cgroup.controllers;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;

public class CGroupIO {
    public static final String IO_STAT = "io.stat";
    public static final String IO_MAX = "io.max";

    private final Path cgroupPath;

    public CGroupIO(Path cgroupPath) throws IOException {
        this.cgroupPath = cgroupPath;

        if (!Files.exists(Path.of(cgroupPath.toString(), "cgroup.controllers"))) {
            throw new IllegalArgumentException("cgroup.controllers file not found. Check your" +
                                                       "cgroup configuration.");
        }

        String availableControllers = Files.readString(Path.of(cgroupPath.toString(), "cgroup" +
                ".controllers"));

        long controllerAvailable = Arrays.stream(availableControllers.split(" "))
                .map(String::strip)
                .filter(controller -> controller.equals("io"))
                .count();

        if (controllerAvailable != 1) {
            throw new IllegalArgumentException("io controller not found in cgroup.controllers " +
                                                       "file." +
                                                       "Check your cgroup configuration.");
        }
    }

    public String getIOStats() throws IOException {
        return Files.readString(Paths.get(cgroupPath.toString(), IO_STAT));
    }

    public void setIOLimitRbps(short deviceMaj,
                               short deviceMin,
                               long rbps) throws IOException {
        Files.writeString(Paths.get(cgroupPath.toString(), IO_MAX),
                          String.format("%d:%d rbps=%d",
                                        deviceMaj, deviceMin, rbps),
                          StandardOpenOption.APPEND);
    }

    public void setIOLimitWbps(short deviceMaj,
                               short deviceMin,
                               long wbps) throws IOException {
        Files.writeString(Paths.get(cgroupPath.toString(), IO_MAX),
                          String.format("%d:%d wbps=%d",
                                        deviceMaj, deviceMin, wbps),
                          StandardOpenOption.APPEND);
    }

    public void setIOLimitRiops(short deviceMaj,
                               short deviceMin,
                               long riops) throws IOException {
        Files.writeString(Paths.get(cgroupPath.toString(), IO_MAX),
                          String.format("%d:%d riops=%d",
                                        deviceMaj, deviceMin, riops),
                          StandardOpenOption.APPEND);
    }

    public void setIOLimitWiops(short deviceMaj,
                                short deviceMin,
                                long wiops) throws IOException {
        Files.writeString(Paths.get(cgroupPath.toString(), IO_MAX),
                          String.format("%d:%d wiops=%d",
                                        deviceMaj, deviceMin, wiops),
                          StandardOpenOption.APPEND);
    }

    public void setIOLimits(short deviceMaj,
                            short deviceMin,
                            long rbps,
                            long wbps,
                            long riops,
                            long wiops) throws IOException {
        Files.writeString(Paths.get(cgroupPath.toString(), IO_MAX),
                          String.format("%d:%d rbps=%d wbps=%d riops=%d wiops=%d",
                                        deviceMaj, deviceMin, rbps, wbps, riops, wiops),
                          StandardOpenOption.APPEND);
    }

    public void removeIOLimits(short deviceMaj,
                               short deviceMin) throws IOException {
        Files.writeString(Paths.get(cgroupPath.toString(), IO_MAX),
                          String.format("%d:%d rbps=max wbps=max riops=max wiops=max",
                                        deviceMaj, deviceMin),
                          StandardOpenOption.APPEND);
    }
}
