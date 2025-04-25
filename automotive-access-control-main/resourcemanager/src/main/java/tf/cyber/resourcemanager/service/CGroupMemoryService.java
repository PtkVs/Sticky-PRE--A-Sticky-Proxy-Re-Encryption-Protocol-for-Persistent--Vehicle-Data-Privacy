package tf.cyber.resourcemanager.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tf.cyber.resourcemanager.cgroup.CGroupFileSystem;

import java.io.IOException;

@Service
public class CGroupMemoryService {
    @Autowired
    private CGroupService cGroupService;

    public String getMemoryCurrent() throws IOException {
        CGroupFileSystem cgfs = cGroupService.getCGroupFileSystem();
        return cgfs.getMemoryManager().getMemoryCurrent();
    }

    public String getMemoryHigh() throws IOException {
        CGroupFileSystem cgfs = cGroupService.getCGroupFileSystem();
        return cgfs.getMemoryManager().getMemoryHigh();
    }

    public String getMemoryMax() throws IOException {
        CGroupFileSystem cgfs = cGroupService.getCGroupFileSystem();
        return cgfs.getMemoryManager().getMemoryMax();
    }

    public String getMemorySwapCurrent() throws IOException {
        CGroupFileSystem cgfs = cGroupService.getCGroupFileSystem();
        return cgfs.getMemoryManager().getMemorySwapCurrent();
    }

    public String getMemorySwapHigh() throws IOException {
        CGroupFileSystem cgfs = cGroupService.getCGroupFileSystem();
        return cgfs.getMemoryManager().getMemorySwapHigh();
    }

    public String getMemorySwapMax() throws IOException {
        CGroupFileSystem cgfs = cGroupService.getCGroupFileSystem();
        return cgfs.getMemoryManager().getMemorySwapMax();
    }

    public void setMemoryHigh(long memoryHigh) throws IOException {
        CGroupFileSystem cgfs = cGroupService.getCGroupFileSystem();
        cgfs.getMemoryManager().setMemoryHigh(memoryHigh);
    }

    public void setMemoryHighUnlimited() throws IOException {
        CGroupFileSystem cgfs = cGroupService.getCGroupFileSystem();
        cgfs.getMemoryManager().setMemoryHighUnlimited();
    }

    public void setMemoryMax(long memoryMax) throws IOException {
        CGroupFileSystem cgfs = cGroupService.getCGroupFileSystem();
        cgfs.getMemoryManager().setMemoryMax(memoryMax);
    }

    public void setMemoryMaxUnlimited() throws IOException {
        CGroupFileSystem cgfs = cGroupService.getCGroupFileSystem();
        cgfs.getMemoryManager().setMemoryMaxUnlimited();
    }

    public void setMemorySwapHigh(long memorySwapHigh) throws IOException {
        CGroupFileSystem cgfs = cGroupService.getCGroupFileSystem();
        cgfs.getMemoryManager().setMemorySwapHigh(memorySwapHigh);
    }

    public void setMemorySwapHighUnlimited() throws IOException {
        CGroupFileSystem cgfs = cGroupService.getCGroupFileSystem();
        cgfs.getMemoryManager().setMemorySwapHighUnlimited();
    }

    public void setMemorySwapMax(long memorySwapMax) throws IOException {
        CGroupFileSystem cgfs = cGroupService.getCGroupFileSystem();
        cgfs.getMemoryManager().setMemorySwapMax(memorySwapMax);
    }

    public void setMemorySwapMaxUnlimited() throws IOException {
        CGroupFileSystem cgfs = cGroupService.getCGroupFileSystem();
        cgfs.getMemoryManager().setMemorySwapMaxUnlimited();
    }
}
