package com.rabobank.statementprocessor.service;

import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

public interface StatementRecordService {

	ResponseEntity<String> process(MultipartFile file);

}