package tf.cyber.resourcemanager.cgroup;

import tf.cyber.resourcemanager.cgroup.controllers.CGroupCPU;
import tf.cyber.resourcemanager.cgroup.controllers.CGroupCPUSet;
import tf.cyber.resourcemanager.cgroup.controllers.CGroupIO;
import tf.cyber.resourcemanager.cgroup.controllers.CGroupMemory;

public interface CGroupFileSystem {
     CGroupCPU getCPUManager();
     CGroupCPUSet getCPUSetManager();
     CGroupMemory getMemoryManager();
     CGroupIO getIOManager();
}
