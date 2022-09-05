package com.ma3ka.vilka.controller;

import com.ma3ka.vilka.service.CSVService;
import com.ma3ka.vilka.service.XLSService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/parsing")
public class ParsingController {

    private XLSService XLSService;
    private CSVService csvService;

    @Autowired
    public ParsingController(XLSService XLSService, CSVService csvService) {
        this.XLSService = XLSService;
        this.csvService = csvService;
    }

    @GetMapping("/start")
    public ResponseEntity startProcess(){
        XLSService.parse();
        return new ResponseEntity<>("Parsing finished!", HttpStatus.OK);
    }


    @GetMapping("/startcsv")
    public ResponseEntity startProcessCSV() throws IOException {
        csvService.parse();
        return new ResponseEntity<>("Parsing finished!", HttpStatus.OK);
    }

    @GetMapping("/save")
    public ResponseEntity saveData(){
        XLSService.save();
        return new ResponseEntity<>("Data saved successfully!", HttpStatus.OK);
    }

    @GetMapping("/load")
    public ResponseEntity loadUnique(){
        XLSService.loadUniqueMarkModels();
        return new ResponseEntity<>("Data saved successfully!", HttpStatus.OK);
    }
}
