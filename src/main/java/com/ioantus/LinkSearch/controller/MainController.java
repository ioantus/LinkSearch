package com.ioantus.LinkSearch.controller;

import com.ioantus.LinkSearch.DTO.ScanResultDTO;
import com.ioantus.LinkSearch.config.AppConstants;
import com.ioantus.LinkSearch.service.MainService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;

import java.util.Map;
import java.util.concurrent.ExecutorService;

@Controller
@SessionAttributes(types = ScanResultDTO.class)
public class MainController {

    @Autowired
    private ExecutorService executorService;

    @GetMapping("/")
    public String handleIndex(
    ) {
        return "index";
    }

    @GetMapping("/run")
    public String handleScan(
            @RequestParam(name = "domain", required = true, defaultValue = "") String domain,
            @RequestParam(name = "maxLevel", required = true, defaultValue = "0") String maxLevel,
            Map<String, Object> model
    ) {
        if (domain == null || domain.equals("")) {
            return  "index";
        } else {
            ScanResultDTO resultDTO;
            String resultDTOName = "resultDTO_"+maxLevel+"_"+domain.toUpperCase();
            if (!model.containsKey(resultDTOName)) {
                MainService workProcess = new MainService(domain, Integer.valueOf(maxLevel));
                resultDTO = new ScanResultDTO(domain, Integer.valueOf(maxLevel), workProcess);
                executorService.submit(workProcess);
            } else {
                resultDTO = (ScanResultDTO)model.get(resultDTOName);
            }
            model.put(resultDTOName, resultDTO);/*
            model.put("domain", resultDTO.getService().getMainDomain());
            model.put("maxLevel", maxLevel);
            model.put("start_time", AppConstants.DF.format(resultDTO.getService().getStartTime()));
            model.put("isDone", resultDTO.getService().idDone());
            model.put("taskAdded", true);
            model.put("resultTable", resultDTO.getService().getResultList());*/
            putResult(model, resultDTO);
            return  "search_result";
        }
    }

    @PostMapping("/update_table")
    public String handleUpdate(
            @RequestParam(name = "domain", required = true, defaultValue = "") String domain,
            @RequestParam(name = "maxLevel", required = true, defaultValue = "0") String maxLevel,
            Map<String, Object> model
    ) {
        if (domain != null && !domain.equals("") && model.containsKey("resultDTO_"+maxLevel+"_"+domain.toUpperCase())) {
            ScanResultDTO resultDTO = (ScanResultDTO)model.get("resultDTO_"+maxLevel+"_"+domain.toUpperCase());
            putResult(model, resultDTO);
            return  "search_result";
        } else {
            return  "index";
        }
    }

    private void putResult(Map<String, Object> model, ScanResultDTO resultDTO) {
        model.put("taskAdded", true);
        model.put("domain", resultDTO.getService().getMainDomain());
        model.put("maxLevel", resultDTO.getLevelScan());
        model.put("internalCount", resultDTO.getService().getInternalPagesCount());
        model.put("externalCount", resultDTO.getService().getExternalPagesCount());
        model.put("start_time", AppConstants.DF.format(resultDTO.getService().getStartTime()));
        model.put("end_time", AppConstants.DF.format(resultDTO.getService().getEndTime()));
        model.put("resultTable",  resultDTO.getService().getResultList());
        model.put("isDone", resultDTO.getService().idDone());
    }

}
