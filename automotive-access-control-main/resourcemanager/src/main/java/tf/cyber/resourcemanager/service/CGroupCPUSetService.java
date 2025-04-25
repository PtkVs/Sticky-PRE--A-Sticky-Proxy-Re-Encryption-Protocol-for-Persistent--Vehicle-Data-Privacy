package tf.cyber.resourcemanager.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tf.cyber.resourcemanager.cgroup.CGroupFileSystem;

import java.io.IOException;
import java.util.Set;

@Service
public class CGroupCPUSetService {
    @Autowired
    private CGroupService cGroupService;

    public Set<Integer> getActiveCPUs() throws IOException {
        CGroupFileSystem cgfs = cGroupService.getCGroupFileSystem();
        return cgfs.getCPUSetManager().getExplicitlyEnabledCPUs();
    }

    public void addCPU(int cpu) throws IOException {
        CGroupFileSystem cgfs = cGroupService.getCGroupFileSystem();
        cgfs.getCPUSetManager().addCPU(cpu);
    }

    public void removeCPU(int cpu) throws IOException {
        CGroupFileSystem cgfs = cGroupService.getCGroupFileSystem();
        cgfs.getCPUSetManager().removeCPU(cpu);
    }
}
