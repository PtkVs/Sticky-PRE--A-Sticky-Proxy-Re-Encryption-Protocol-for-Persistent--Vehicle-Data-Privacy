package tf.cyber.resourcemanager.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import tf.cyber.resourcemanager.pep.annotation.XACMLAccessControl;
import tf.cyber.resourcemanager.pep.annotation.attributes.Resource;
import tf.cyber.resourcemanager.service.CGroupIOService;

import java.io.IOException;

@RestController
@RequestMapping("/io")
@XACMLAccessControl
public class IOController {
    @Autowired
    private CGroupIOService cGroupIOService;

    @RequestMapping("/rbps")
    @ResponseBody
    public String setIORbps(@Resource("urn:tf:cyber:resourcecontrol:io:devicemaj") Short deviceMaj,
                            @Resource("urn:tf:cyber:resourcecontrol:io:devicemin") Short deviceMin,
                            @Resource("urn:tf:cyber:resourcecontrol:io:rbps") Long rbps) throws IOException {
        cGroupIOService.setIOLimitRbps(deviceMaj, deviceMin, rbps);
        return "OK";
    }

    @RequestMapping("/wbps")
    @ResponseBody
    public String setIOWbps(@Resource("urn:tf:cyber:resourcecontrol:io:devicemaj") Short deviceMaj,
                            @Resource("urn:tf:cyber:resourcecontrol:io:devicemin") Short deviceMin,
                            @Resource("urn:tf:cyber:resourcecontrol:io:wbps") Long wbps) throws IOException {
        cGroupIOService.setIOLimitWbps(deviceMaj, deviceMin, wbps);
        return "OK";
    }

    @RequestMapping("/riops")
    @ResponseBody
    public String setIORiops(@Resource("urn:tf:cyber:resourcecontrol:io:devicemaj") Short deviceMaj,
                             @Resource("urn:tf:cyber:resourcecontrol:io:devicemin") Short deviceMin,
                             @Resource("urn:tf:cyber:resourcecontrol:io:riops") Long riops) throws IOException {
        cGroupIOService.setIOLimitRiops(deviceMaj, deviceMin, riops);
        return "OK";
    }

    @RequestMapping("/wiops")
    @ResponseBody
    public String setIOWiops(@Resource("urn:tf:cyber:resourcecontrol:io:devicemaj") Short deviceMaj,
                             @Resource("urn:tf:cyber:resourcecontrol:io:devicemin") Short deviceMin,
                             @Resource("urn:tf:cyber:resourcecontrol:io:wiops") Long wiops) throws IOException {
        cGroupIOService.setIOLimitWiops(deviceMaj, deviceMin, wiops);
        return "OK";
    }

    @RequestMapping("/")
    @ResponseBody
    public String setIOLimits(@Resource("urn:tf:cyber:resourcecontrol:io:devicemaj") Short deviceMaj,
                              @Resource("urn:tf:cyber:resourcecontrol:io:devicemin") Short deviceMin,
                              @Resource("urn:tf:cyber:resourcecontrol:io:rbps") Long rbps,
                              @Resource("urn:tf:cyber:resourcecontrol:io:wbps") Long wbps,
                              @Resource("urn:tf:cyber:resourcecontrol:io:riops") Long riops,
                              @Resource("urn:tf:cyber:resourcecontrol:io:wiops") Long wiops) throws IOException {
        cGroupIOService.setIOLimits(deviceMaj, deviceMin, rbps, wbps, riops, wiops);
        return "OK";
    }

    @RequestMapping("/unlimited")
    @ResponseBody
    public String setIOWiops(@Resource("urn:tf:cyber:resourcecontrol:io:devicemaj") Short deviceMaj,
                             @Resource("urn:tf:cyber:resourcecontrol:io:devicemin") Short deviceMin) throws IOException {
        cGroupIOService.removeIOLimits(deviceMaj, deviceMin);
        return "OK";
    }
}
