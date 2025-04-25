package tf.cyber.resourcemanager.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tf.cyber.resourcemanager.cgroup.CGroupFileSystem;

import java.io.IOException;

@Service
public class CGroupCPUService {
    @Autowired
    private CGroupService cGroupService;

    public void setNiceValue(int niceValue) throws IOException {
        CGroupFileSystem cgfs = cGroupService.getCGroupFileSystem();
        cgfs.getCPUManager().setCpuNice(niceValue);
    }

    public void setCPUMax(int time) throws IOException {
        CGroupFileSystem cgfs = cGroupService.getCGroupFileSystem();
        cgfs.getCPUManager().setCpuMax(time, 100000);
    }

    public void setCPUMax(int time, int fraction) throws IOException {
        CGroupFileSystem cgfs = cGroupService.getCGroupFileSystem();
        cgfs.getCPUManager().setCpuMax(time, fraction);
    }

    public void setCPUMaxUnlimited() throws IOException {
        CGroupFileSystem cgfs = cGroupService.getCGroupFileSystem();
        cgfs.getCPUManager().setCPUMaxUnlimited();
    }
}
