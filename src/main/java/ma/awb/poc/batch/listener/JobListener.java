package ma.awb.poc.batch.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.listener.JobExecutionListenerSupport;
import org.springframework.util.StopWatch;

public class JobListener extends JobExecutionListenerSupport {

	private static final Logger logger = LoggerFactory.getLogger(JobListener.class);

	private StopWatch watch;

	@Override
	public void beforeJob(JobExecution jobExecution) {
		watch = new StopWatch();
		watch.start();
		logger.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
		logger.info(">> Début execution Job {} At {}", jobExecution.getId(), jobExecution.getStartTime());
		logger.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
	}

	@Override
	public void afterJob(JobExecution jobExecution) {
		watch.stop();
		switch (jobExecution.getExitStatus().getExitCode()) {
		case "COMPLETED":
			logger.info("<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");
			logger.info("<< Fin execution Job {} At {} status {} duration {} seconds", jobExecution.getId(),
					jobExecution.getEndTime(), jobExecution.getExitStatus(), watch.getTotalTimeSeconds());
			logger.info("<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");
			break;
		default:
			logger.error("<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");
			logger.error("<< Fin execution Job {} At {} status {} duration {} seconds", jobExecution.getId(),
					jobExecution.getEndTime(), jobExecution.getExitStatus(), watch.getTotalTimeSeconds());
			logger.error("<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");
			break;
		}

	}
}
