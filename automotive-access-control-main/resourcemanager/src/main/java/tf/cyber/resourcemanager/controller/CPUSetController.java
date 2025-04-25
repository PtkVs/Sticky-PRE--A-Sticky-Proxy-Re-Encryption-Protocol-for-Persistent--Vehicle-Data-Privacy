package tf.cyber.resourcemanager.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import tf.cyber.resourcemanager.pep.annotation.XACMLAccessControl;
import tf.cyber.resourcemanager.pep.annotation.attributes.Resource;
import tf.cyber.resourcemanager.service.CGroupCPUSetService;

import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.util.Set;

@RestController
@RequestMapping("/cpuset")
@XACMLAccessControl
public class CPUSetController {
    @Autowired
    CGroupCPUSetService cGroupCPUSetService;

    @RequestMapping(value = "/", method = RequestMethod.GET)
    @ResponseBody
    public Set<Integer> getActiveCPUs() throws IOException {
        return cGroupCPUSetService.getActiveCPUs();
    }

    @RequestMapping(value = "/add", method = RequestMethod.POST)
    @ResponseBody
    public String addCPU(@Resource("urn:tf:cyber:resourcecontrol:cpuset:add") int cpu) throws IOException {
        cGroupCPUSetService.addCPU(cpu);
        return "OK";
    }

    @RequestMapping(value = "/remove", method = RequestMethod.POST)
    @ResponseBody
    public String removeCPU(@Resource("urn:tf:cyber:resourcecontrol:cpuset:remove") int cpu) throws IOException {
        if (cGroupCPUSetService.getActiveCPUs().size() == 1) {
            throw new AccessDeniedException("Tried to remove the only remaining CPU from the CPU set." +
                                                    "This would imply lifting all restrictions.");
        }

        cGroupCPUSetService.removeCPU(cpu);
        return "OK";
    }
}
