package com.rabobank.statementprocessor.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.rabobank.statementprocessor.utils.Constants;

@Service
public class StatementRecordServiceImpl implements StatementRecordService {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(StatementRecordServiceImpl.class);
	
	private JobLauncher jobLauncher;
	private Job processJob;
	private Environment environment;
	private String folderPath;
	
	@Autowired
    public StatementRecordServiceImpl(JobLauncher jobLauncher, Job processJob,Environment environment) {
        this.jobLauncher = jobLauncher;
        this.processJob = processJob;
        this.environment = environment;
    }
	
	@Override
	public ResponseEntity<String> process(MultipartFile file) {
		String filePath=null;
		LOGGER.info("process started");
		folderPath=environment.getProperty("container");
		
		try {	
			 filePath = readFile(file);
			 if (filePath.toLowerCase().endsWith(".csv") || filePath.toLowerCase().endsWith(".xml") )
				 runBatchJob(filePath);
			 else {
				 LOGGER.warn("csv or xml file is missing");
				 return new ResponseEntity<>(Constants.CSV_OR_XML,  HttpStatus.BAD_REQUEST);
			 }
			return new ResponseEntity<>(Constants.FILE_PATH+folderPath,  HttpStatus.OK);
		} catch (IOException e) {
			LOGGER.error("Input File is missing");
			return new ResponseEntity<>(Constants.ERROR_MESSAGE,  HttpStatus.BAD_REQUEST);		
		}
		
		

	}

	@Async
	private void runBatchJob(String filePath) {
		JobParametersBuilder jobParametersBuilder = new JobParametersBuilder();
		jobParametersBuilder.addString(Constants.INPUT_FILE, filePath);
		runJob(processJob,jobParametersBuilder.toJobParameters());
	}

	private void runJob(Job job, JobParameters jobParameters) {
		try {
			jobLauncher.run(job, jobParameters );
		} catch (JobExecutionAlreadyRunningException | JobRestartException | JobInstanceAlreadyCompleteException
				| JobParametersInvalidException e) {
			LOGGER.info("Job with inputFile={} is already running.", jobParameters.getParameters().get(Constants.INPUT_FILE));
		}
	}

	private String readFile(MultipartFile file) throws IOException {
		Path path = Paths.get(folderPath + file.getOriginalFilename());
		Files.write(path, file.getBytes());
		return path.toString();
	}

}
