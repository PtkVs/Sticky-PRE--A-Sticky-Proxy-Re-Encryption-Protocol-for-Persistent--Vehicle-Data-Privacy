package tf.cyber.resourcemanager.cgroup.controllers;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class CGroupCPUSet {
    public static final String CPUSET_CPUS = "cpuset.cpus";

    private final Path cgroupPath;

    public CGroupCPUSet(Path cgroupPath) throws IOException {
        this.cgroupPath = cgroupPath;

        if (!Files.exists(Path.of(cgroupPath.toString(), "cgroup.controllers"))) {
            throw new IllegalArgumentException("cgroup.controllers file not found. Check your" +
                                                       "cgroup configuration.");
        }

        String availableControllers = Files.readString(Path.of(cgroupPath.toString(), "cgroup" +
                ".controllers"));

        long controllerAvailable = Arrays.stream(availableControllers.split(" "))
                .map(String::strip)
                .filter(controller -> controller.equals("cpuset"))
                .count();

        if (controllerAvailable != 1) {
            throw new IllegalArgumentException("cpuset controller not found in cgroup.controllers " +
                                                       "file." +
                                                       "Check your cgroup configuration.");
        }
    }

    public Set<Integer> getExplicitlyEnabledCPUs() throws IOException {
        Set<Integer> cpus = new HashSet<>();

        String cpuRaw = Files.readString(Paths.get(cgroupPath.toString(), CPUSET_CPUS)).strip();
        String[] cpuArr = cpuRaw.split(",");

        for (String cpuDef : cpuArr) {
            if (cpuDef.contains("-")) {
                // is range
                String[] cpuStartEnd = cpuDef.split("-");
                int start = Integer.parseInt(cpuStartEnd[0]);
                int end = Integer.parseInt(cpuStartEnd[1]);

                cpus.addAll(IntStream.rangeClosed(start, end).boxed().collect(Collectors.toList()));

            } else {
               cpus.add(Integer.parseInt(cpuDef));
            }
        }

        return cpus;
    }

    public void addCPU(int cpu) throws IOException {
        Set<Integer> cpus = getExplicitlyEnabledCPUs();
        cpus.add(cpu);

        Set<String> cpusString = cpus.stream().map(Object::toString).collect(Collectors.toSet());

        Files.writeString(Paths.get(cgroupPath.toString(), CPUSET_CPUS),
                          String.join(",", cpusString),
                          StandardOpenOption.WRITE);
    }

    public void removeCPU(int cpu) throws IOException {
        Set<Integer> cpus = getExplicitlyEnabledCPUs();
        cpus.remove(cpu);

        Set<String> cpusString = cpus.stream().map(Object::toString).collect(Collectors.toSet());

        Files.writeString(Paths.get(cgroupPath.toString(), CPUSET_CPUS),
                          String.join(",", cpusString),
                          StandardOpenOption.WRITE);
    }
}
