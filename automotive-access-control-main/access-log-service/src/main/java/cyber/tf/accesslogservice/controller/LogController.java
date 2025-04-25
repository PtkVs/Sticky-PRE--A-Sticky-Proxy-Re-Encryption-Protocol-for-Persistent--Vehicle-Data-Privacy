package cyber.tf.accesslogservice.controller;

import cyber.tf.accesslogservice.model.LogEntry;
import cyber.tf.accesslogservice.service.LogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/log")
public class LogController {

    @Autowired
    LogService logService;

    @RequestMapping(value = "/save", method = RequestMethod.POST)
    @ResponseBody
    public LogEntry log(@RequestBody LogEntry entry) {
        return logService.storeEntry(entry);
    }

    @RequestMapping(value = "/query", method = RequestMethod.POST)
    @ResponseBody
    public LogEntry queryLastAccessSubject(@RequestBody LogEntry entry) {
        LogEntry res = logService.queryLastAccess(entry);

        if (res == null) {
            res = new LogEntry(entry.getSubject(), entry.getAction(), entry.getResource(), 0);
        }

        return res;
    }
}
