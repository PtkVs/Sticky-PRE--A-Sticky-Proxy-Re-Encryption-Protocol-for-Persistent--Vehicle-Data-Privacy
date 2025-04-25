package tf.cyber.resourcemanager.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import tf.cyber.resourcemanager.pep.annotation.XACMLAccessControl;
import tf.cyber.resourcemanager.pep.annotation.attributes.Resource;
import tf.cyber.resourcemanager.service.CGroupCPUService;

import java.io.IOException;

@RestController
@RequestMapping("/cpu")
@XACMLAccessControl
public class CPUController {
    @Autowired
    private CGroupCPUService cGroupCPUService;

    @RequestMapping(value = "/nice", method = RequestMethod.POST)
    @ResponseBody
    public String setNice(@Resource("urn:tf:cyber:resourcecontrol:cpu:nice") Integer niceValue) throws IOException {
        if (niceValue == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No niceValue parameter " +
                    "specified.", new IllegalArgumentException());
        }

        cGroupCPUService.setNiceValue(niceValue);
        return "OK";
    }

    @RequestMapping(value = "/time", method = RequestMethod.POST)
    public String setCPUTime(@Resource("urn:tf:cyber:resourcecontrol:cpu:time") Integer time) throws IOException {
        if (time == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No time parameter " +
                    "specified.", new IllegalArgumentException());
        }

        cGroupCPUService.setCPUMax(time);
        return "OK";
    }

    @RequestMapping(value = "/time/unlimited", method = RequestMethod.POST)
    public String setCPUTimeMax() throws IOException {
        cGroupCPUService.setCPUMaxUnlimited();
        return "OK";
    }

    @RequestMapping(value = "/time/fractional", method = RequestMethod.POST)
    public String setCPUTimeWithFraction(@Resource("urn:tf:cyber:resourcecontrol:cpu:time") Integer time,
                                         @Resource("urn:tf:cyber:resourcecontrol:cpu:fraction") Integer fraction)
            throws IOException {
        if (time == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No time parameter " +
                    "specified.", new IllegalArgumentException());
        }

        if (fraction == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No fraction parameter " +
                    "specified.", new IllegalArgumentException());
        }

        cGroupCPUService.setCPUMax(time, fraction);
        return "OK";
    }
}
