package tf.cyber.resourcemanager.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import tf.cyber.resourcemanager.pep.annotation.XACMLAccessControl;
import tf.cyber.resourcemanager.pep.annotation.attributes.Resource;
import tf.cyber.resourcemanager.service.CGroupMemoryService;

import java.io.IOException;

@RestController
@RequestMapping("/memory")
@XACMLAccessControl
public class MemoryController {
    @Autowired
    private CGroupMemoryService cGroupMemoryService;

    @RequestMapping(value = "/current", method = RequestMethod.GET)
    public String getMemoryCurrent() throws IOException {
        return cGroupMemoryService.getMemoryCurrent();
    }

    @RequestMapping(value = "/high", method = RequestMethod.GET)
    public String getMemoryHigh() throws IOException {
        return cGroupMemoryService.getMemoryHigh();
    }

    @RequestMapping(value = "/max", method = RequestMethod.GET)
    public String getMemoryMax() throws IOException {
        return cGroupMemoryService.getMemoryMax();
    }

    @RequestMapping(value = "/swap_current", method = RequestMethod.GET)
    public String getMemorySwapCurrent() throws IOException {
        return cGroupMemoryService.getMemorySwapCurrent();
    }

    @RequestMapping(value = "/swap_high", method = RequestMethod.GET)
    public String getMemorySwapHigh() throws IOException {
        return cGroupMemoryService.getMemorySwapHigh();
    }

    @RequestMapping(value = "/swap_max", method = RequestMethod.GET)
    public String getMemorySwapMax() throws IOException {
        return cGroupMemoryService.getMemorySwapMax();
    }

    // ----------------------------------------------------------------------------------

    @RequestMapping(value = "/high", method = RequestMethod.POST)
    @ResponseBody
    public String setMemoryHigh(@Resource("urn:tf:cyber:resourcecontrol:memory:high") Long memoryHigh) throws IOException {
        if (memoryHigh == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No memoryHigh parameter " +
                    "specified.", new IllegalArgumentException());
        }

        cGroupMemoryService.setMemoryHigh(memoryHigh);
        return "OK";
    }

    @RequestMapping(value = "/high/unlimited", method = RequestMethod.POST)
    @ResponseBody
    public String setMemoryHighUnlimited() throws IOException {
        cGroupMemoryService.setMemoryHighUnlimited();
        return "OK";
    }

    @RequestMapping(value = "/max", method = RequestMethod.POST)
    @ResponseBody
    public String setMemoryMax(@Resource("urn:tf:cyber:resourcecontrol:memory:max") Long memoryMax) throws IOException {
        if (memoryMax == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No memoryMax parameter " +
                    "specified.", new IllegalArgumentException());
        }

        cGroupMemoryService.setMemoryMax(memoryMax);
        return "OK";
    }

    @RequestMapping(value = "/max/unlimited", method = RequestMethod.POST)
    @ResponseBody
    public String setMemoryMaxUnlimited() throws IOException {
        cGroupMemoryService.setMemoryMaxUnlimited();
        return "OK";
    }

    @RequestMapping(value = "/swap_high", method = RequestMethod.POST)
    @ResponseBody
    public String setMemorySwapHigh(@Resource("urn:tf:cyber:resourcecontrol:memory:swap:high") Long swapHigh) throws IOException {
        if (swapHigh == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No swapHigh parameter " +
                    "specified.", new IllegalArgumentException());
        }

        cGroupMemoryService.setMemorySwapHigh(swapHigh);
        return "OK";
    }

    @RequestMapping(value = "/swap_high/unlimited", method = RequestMethod.POST)
    @ResponseBody
    public String setMemorySwapHighUnlimited() throws IOException {
        cGroupMemoryService.setMemorySwapHighUnlimited();
        return "OK";
    }

    @RequestMapping(value = "/swap_max", method = RequestMethod.POST)
    @ResponseBody
    public String setMemorySwapMax(@Resource("urn:tf:cyber:resourcecontrol:memory:swap:max") Long swapMax) throws IOException {
        if (swapMax == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No swapMax parameter " +
                    "specified.", new IllegalArgumentException());
        }

        cGroupMemoryService.setMemorySwapMax(swapMax);
        return "OK";
    }

    @RequestMapping(value = "/swap_max/unlimited", method = RequestMethod.POST)
    @ResponseBody
    public String setMemorySwapMaxUnlimited() throws IOException {
        cGroupMemoryService.setMemorySwapMaxUnlimited();
        return "OK";
    }
}
