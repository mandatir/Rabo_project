package com.rabobank.statementprocessor.controller;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.rabobank.statementprocessor.service.StatementRecordService;
import com.rabobank.statementprocessor.utils.Constants;

@RestController
@RequestMapping("customer/api/")
public class StatementRecordController {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(StatementRecordController.class);

	private StatementRecordService statementRecordService;
	
	@Autowired
    public StatementRecordController(StatementRecordService statementRecordService) {
        this.statementRecordService = statementRecordService;
    }
	
	@PostMapping("process-statement")
	 public ResponseEntity<String> processFile(@NonNull @RequestParam("file") MultipartFile file) {
		LOGGER.info("processFile started");  
		return file.isEmpty() ?
			                   new ResponseEntity<String>(Constants.ERROR_MESSAGE,  HttpStatus.NOT_FOUND) 
			                  : statementRecordService.process(file);
	    }
}
