package tf.cyber.resourcemanager.cgroup.controllers;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;

public class CGroupMemory {
    public static final String MEMORY_CURRENT = "memory.current";
    public static final String MEMORY_HIGH = "memory.high";
    public static final String MEMORY_MAX = "memory.max";

    public static final String MEMORY_SWAP_CURRENT = "memory.swap.current";
    public static final String MEMORY_SWAP_HIGH = "memory.swap.high";
    public static final String MEMORY_SWAP_MAX = "memory.swap.max";

    private final Path cgroupPath;

    public CGroupMemory(Path cgroupPath) throws IOException {
        this.cgroupPath = cgroupPath;

        if (!Files.exists(Path.of(cgroupPath.toString(), "cgroup.controllers"))) {
            throw new IllegalArgumentException("cgroup.controllers file not found. Check your" +
                                                       "cgroup configuration.");
        }

        String availableControllers = Files.readString(Path.of(cgroupPath.toString(), "cgroup.controllers"));

        long controllerAvailable = Arrays.stream(availableControllers.split(" "))
                .map(String::strip)
                .filter(controller -> controller.equals("memory"))
                .count();

        if (controllerAvailable != 1) {
            throw new IllegalArgumentException("memory controller not found in cgroup.controllers file." +
                                                       "Check your cgroup configuration.");
        }
    }

    public String getMemoryCurrent() throws IOException {
        return Files.readString(Paths.get(cgroupPath.toString(), MEMORY_CURRENT));
    }

    public String getMemoryHigh() throws IOException {
        return Files.readString(Paths.get(cgroupPath.toString(), MEMORY_HIGH));
    }

    public String getMemoryMax() throws IOException {
        return Files.readString(Paths.get(cgroupPath.toString(), MEMORY_MAX));
    }

    public String getMemorySwapCurrent() throws IOException {
        return Files.readString(Paths.get(cgroupPath.toString(), MEMORY_SWAP_CURRENT));
    }

    public String getMemorySwapHigh() throws IOException {
        return Files.readString(Paths.get(cgroupPath.toString(), MEMORY_SWAP_HIGH));
    }

    public String getMemorySwapMax() throws IOException {
        return Files.readString(Paths.get(cgroupPath.toString(), MEMORY_SWAP_MAX));
    }

    public void setMemoryHigh(long memoryHigh) throws IOException {
        if (memoryHigh < 1) {
            throw new IllegalArgumentException("Memory limit must be greater than 1 byte.");
        }

        Files.writeString(Paths.get(cgroupPath.toString(), MEMORY_HIGH),
                          Long.toString(memoryHigh),
                          StandardOpenOption.WRITE);
    }

    public void setMemoryHighUnlimited() throws IOException {
        Files.writeString(Paths.get(cgroupPath.toString(), MEMORY_HIGH),
                          "max",
                          StandardOpenOption.WRITE);
    }

    public void setMemoryMax(long memoryMax) throws IOException {
        if (memoryMax < 1) {
            throw new IllegalArgumentException("Memory limit must be greater than 1 byte.");
        }

        Files.writeString(Paths.get(cgroupPath.toString(), MEMORY_MAX),
                          Long.toString(memoryMax),
                          StandardOpenOption.WRITE);
    }

    public void setMemoryMaxUnlimited() throws IOException {
        Files.writeString(Paths.get(cgroupPath.toString(), MEMORY_MAX),
                          "max",
                          StandardOpenOption.WRITE);
    }

    public void setMemorySwapHigh(long memorySwapHigh) throws IOException {
        if (memorySwapHigh < 1) {
            throw new IllegalArgumentException("Memory limit must be greater than 1 byte.");
        }

        Files.writeString(Paths.get(cgroupPath.toString(), MEMORY_SWAP_HIGH),
                          Long.toString(memorySwapHigh),
                          StandardOpenOption.WRITE);
    }

    public void setMemorySwapHighUnlimited() throws IOException {
        Files.writeString(Paths.get(cgroupPath.toString(), MEMORY_SWAP_HIGH),
                          "max",
                          StandardOpenOption.WRITE);
    }

    public void setMemorySwapMax(long memorySwapMax) throws IOException {
        if (memorySwapMax < 1) {
            throw new IllegalArgumentException("Memory limit must be greater than 1 byte.");
        }

        Files.writeString(Paths.get(cgroupPath.toString(), MEMORY_SWAP_MAX),
                          Long.toString(memorySwapMax),
                          StandardOpenOption.WRITE);
    }

    public void setMemorySwapMaxUnlimited() throws IOException {
        Files.writeString(Paths.get(cgroupPath.toString(), MEMORY_SWAP_MAX),
                          "max",
                          StandardOpenOption.WRITE);
    }
}
