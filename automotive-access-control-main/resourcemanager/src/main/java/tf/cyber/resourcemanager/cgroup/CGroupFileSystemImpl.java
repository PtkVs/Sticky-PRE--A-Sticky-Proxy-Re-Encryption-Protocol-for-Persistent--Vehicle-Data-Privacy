package tf.cyber.resourcemanager.cgroup;

import tf.cyber.resourcemanager.cgroup.controllers.CGroupCPU;
import tf.cyber.resourcemanager.cgroup.controllers.CGroupCPUSet;
import tf.cyber.resourcemanager.cgroup.controllers.CGroupIO;
import tf.cyber.resourcemanager.cgroup.controllers.CGroupMemory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class CGroupFileSystemImpl implements CGroupFileSystem {
    private CGroupCPU CGroupCPU;
    private CGroupCPUSet cGroupCPUSet;
    private CGroupMemory cGroupMemory;
    private CGroupIO cGroupIO;

    public CGroupFileSystemImpl(Path baseCgroupFs, String user) throws IOException {
        if (baseCgroupFs == null) {
            throw new IllegalArgumentException("No base cgroupfs path specified. Check cgroup.fs.homedir " +
                                                       "in application.properties.");
        }

        Path userFs = Path.of(baseCgroupFs.toString(), user);
        String jvmUser = System.getProperty("user.name");

        if (!Files.isDirectory(baseCgroupFs)) {
            throw new IllegalArgumentException("Invalid base cgroup path specified.");
        }

        if (!Files.isDirectory(userFs)) {
            throw new IllegalArgumentException("cgroup for user " + user + " not found. Check your" +
                                                       "/etc/cgconfig.conf.");
        }

        if (!Files.exists(Paths.get(userFs.toString(), "cgroup.controllers"))) {
            throw new IllegalArgumentException("Could not find cgroup.controllers file. " +
                                                       "Check your cgroup configuration.");
        }

        if (!Files.getOwner(userFs).getName().equals(jvmUser)) {
            throw new IllegalArgumentException("cgroupfs is not owned by resource manager. " +
                                                       "Check your configuration " +
                                                       "in /etc/cgconfig.conf and make sure " +
                                                       "that the JVM is running via the " +
                                                       "corresponding user.");
        }

        this.CGroupCPU = new CGroupCPU(userFs);
        this.cGroupCPUSet = new CGroupCPUSet(userFs);
        this.cGroupMemory = new CGroupMemory(userFs);
        this.cGroupIO = new CGroupIO(userFs);
    }

    @Override
    public CGroupCPU getCPUManager() {
        return CGroupCPU;
    }

    @Override
    public CGroupCPUSet getCPUSetManager() {
        return cGroupCPUSet;
    }

    @Override
    public CGroupMemory getMemoryManager() {
        return cGroupMemory;
    }

    @Override
    public CGroupIO getIOManager() {
        return cGroupIO;
    }
}
