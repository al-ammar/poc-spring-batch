package ma.awb.poc.core;

import java.util.Date;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/batch")
@RestController
public class LaunchController {

	@Autowired
	private JobLauncher jobLauncher;

	@Qualifier("remotePartitioning")
	@Autowired
	private Job job;
	
	@GetMapping
	public ResponseEntity<?> launch() throws JobExecutionAlreadyRunningException, JobRestartException,
			JobInstanceAlreadyCompleteException, JobParametersInvalidException {
		final JobParameters parameters = new JobParametersBuilder().addDate("time", new Date()).toJobParameters();
		final JobExecution execution = jobLauncher.run(job, parameters);
		return ResponseEntity.noContent().build();
	}

}
