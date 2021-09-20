package com.ioantus.LinkSearch.controller;

import com.ioantus.LinkSearch.DTO.ScanResultDTO;
import com.ioantus.LinkSearch.service.MainService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutorService;

@Controller
@SessionAttributes(types = ScanResultDTO.class)
public class MainController {

    @Autowired
    private ApplicationContext ctx;

    @GetMapping("/")
    public String handleIndex(
            //@RequestParam(name = "uuid", required = true, defaultValue = "") String uuid,
            Map<String, Object> model
    ) {
        /*if (uuid == null || uuid.equals("")) {
            uuid = UUID.randomUUID().toString();
            model.put("uuid", uuid);
        }*/
        return  "index";
    }

    @GetMapping("/run")
    public String handleIndex(
            @RequestParam(name = "domain", required = true, defaultValue = "") String domain,
            @RequestParam(name = "maxLevel", required = true, defaultValue = "1") String maxLevel,
            //@RequestParam(name = "uuid", required = true, defaultValue = "") String uuid,
            Map<String, Object> model
    ) {
        /*if (uuid == null || uuid.equals("")) {
            uuid = UUID.randomUUID().toString();
            model.put("uuid", uuid);
        }*/
        if (domain == null || domain.equals("")) {
            return  "index";
        } else {
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
            MainService workProcess = new MainService(domain, Integer.valueOf(maxLevel));
            ScanResultDTO resultDTO = new ScanResultDTO(domain, Integer.valueOf(maxLevel), workProcess);
            //((ExecutorService)ctx.getBean("executorService")).submit(workProcess);
            model.put("taskAdded", true);
            model.put("domain", domain);
            model.put("start_time", dateFormat.format(new Date()));
            model.put("result", resultDTO);
            model.put("resultTable", false);
            return  "search_result";
        }
    }

}
