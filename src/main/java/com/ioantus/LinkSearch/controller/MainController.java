package com.ioantus.LinkSearch.controller;

import com.ioantus.LinkSearch.DTO.ScanResultDTO;
import com.ioantus.LinkSearch.DTO.SinglePageDTO;
import com.ioantus.LinkSearch.config.AppConstants;
import com.ioantus.LinkSearch.report.ReportBuilder;
import com.ioantus.LinkSearch.service.MainService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.HashSet;
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
            String resultName = formatResultName(maxLevel, domain.toUpperCase());
            if (!model.containsKey(resultName)) {
                resultDTO = new ScanResultDTO(domain, Integer.valueOf(maxLevel), new HashSet<SinglePageDTO>());
                MainService service = new MainService(resultDTO);
                executorService.submit(service);
            } else {
                resultDTO = (ScanResultDTO) model.get(resultName);
            }
            model.put(resultName, resultDTO);
            putResult(model, resultDTO);
            return  "result";
        }
    }

    @PostMapping("/refresh")
    public String handleUpdate(
            @RequestParam(name = "domain", required = true, defaultValue = "") String domain,
            @RequestParam(name = "maxLevel", required = true, defaultValue = "0") String maxLevel,
            Map<String, Object> model
    ) {
        if (domain != null && !domain.equals("") && model.containsKey(formatResultName(maxLevel, domain))) {
            ScanResultDTO resultDTO = (ScanResultDTO)model.get(formatResultName(maxLevel, domain));
            putResult(model, resultDTO);
            return  "result";
        } else {
            return  "index";
        }
    }


    @GetMapping(value = "/report/{maxLevel}_{domain}.xlsx")
    public void getReport(
            @PathVariable("domain") String domain,
            @PathVariable("maxLevel") String maxLevel,
            HttpServletResponse response,
            Map<String, Object> model
    ) {
        try {
            if (domain != null && !domain.equals("") && model.containsKey(formatResultName(maxLevel, domain))) {
                ScanResultDTO resultDTO = (ScanResultDTO)model.get(formatResultName(maxLevel, domain));
                ReportBuilder.createReport(resultDTO, response.getOutputStream());
                response.flushBuffer();
            }
        } catch (IOException ex) {
            AppConstants.LOGGER.error(String.format("Error writing file to output stream, domain %s", domain));
            throw new RuntimeException("IOError writing file to output stream");
        }

    }

    private String formatResultName(String maxLevel, String domain){
        return "domain_"+maxLevel+"_"+domain.toUpperCase();
    }

    private void putResult(Map<String, Object> model, ScanResultDTO resultDTO) {
        model.put("taskAdded", true);
        model.put("domain", resultDTO.getDomain());
        model.put("maxLevel", resultDTO.getLevelScan());
        model.put("internalCount", resultDTO.getResultSet().size());
        model.put("externalCount", resultDTO.getExternalPagesCount());
        model.put("start_time", AppConstants.DF.format(resultDTO.getStartTime()));
        model.put("end_time", AppConstants.DF.format(resultDTO.getEndTime()));
        model.put("resultTable",  resultDTO.getResultSet());
        model.put("isDone",resultDTO.isJobDone());
    }

}
