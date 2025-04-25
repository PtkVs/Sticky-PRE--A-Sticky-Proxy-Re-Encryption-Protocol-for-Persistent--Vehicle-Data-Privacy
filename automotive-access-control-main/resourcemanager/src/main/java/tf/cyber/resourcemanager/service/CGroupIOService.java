package tf.cyber.resourcemanager.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tf.cyber.resourcemanager.cgroup.CGroupFileSystem;

import java.io.IOException;

@Service
public class CGroupIOService {
    @Autowired
    private CGroupService cGroupService;

    public String getIOStats() throws IOException {
        CGroupFileSystem cgfs = cGroupService.getCGroupFileSystem();
        return cgfs.getIOManager().getIOStats();
    }

    public void setIOLimitRbps(short deviceMaj,
                                 short deviceMin,
                                 long rbps) throws IOException {
        CGroupFileSystem cgfs = cGroupService.getCGroupFileSystem();
        cgfs.getIOManager().setIOLimitRbps(deviceMaj, deviceMin, rbps);
    }

    public void setIOLimitWbps(short deviceMaj,
                               short deviceMin,
                               long wbps) throws IOException {
        CGroupFileSystem cgfs = cGroupService.getCGroupFileSystem();
        cgfs.getIOManager().setIOLimitWbps(deviceMaj, deviceMin, wbps);
    }

    public void setIOLimitRiops(short deviceMaj,
                               short deviceMin,
                               long riops) throws IOException {
        CGroupFileSystem cgfs = cGroupService.getCGroupFileSystem();
        cgfs.getIOManager().setIOLimitRiops(deviceMaj, deviceMin, riops);
    }

    public void setIOLimitWiops(short deviceMaj,
                                short deviceMin,
                                long wiops) throws IOException {
        CGroupFileSystem cgfs = cGroupService.getCGroupFileSystem();
        cgfs.getIOManager().setIOLimitWiops(deviceMaj, deviceMin, wiops);
    }

    public void setIOLimits(short deviceMaj,
                            short deviceMin,
                            long rbps,
                            long wbps,
                            long riops,
                            long wiops) throws IOException {
        CGroupFileSystem cgfs = cGroupService.getCGroupFileSystem();
        cgfs.getIOManager().setIOLimits(deviceMaj, deviceMin, rbps, wbps, riops, wiops);
    }

    public void removeIOLimits(short deviceMaj,
                               short deviceMin) throws IOException {
        CGroupFileSystem cgfs = cGroupService.getCGroupFileSystem();
        cgfs.getIOManager().removeIOLimits(deviceMaj, deviceMin);
    }
}
