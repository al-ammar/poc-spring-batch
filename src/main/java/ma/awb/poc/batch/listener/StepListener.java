package ma.awb.poc.batch.listener;

import java.time.Instant;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.listener.StepExecutionListenerSupport;
import org.springframework.util.StopWatch;

public class StepListener extends StepExecutionListenerSupport {

	private static final Logger logger = LoggerFactory.getLogger(StepListener.class);

	private StopWatch watch;

	@Override
	public void beforeStep(StepExecution stepExecution) {
		this.watch = new StopWatch();
		watch.start();
		logger.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
		logger.info(">> Début Step {} At {}", stepExecution.getStepName(), stepExecution.getStartTime());
		logger.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
	}

	@Override
	public ExitStatus afterStep(StepExecution stepExecution) {
		this.watch.stop();
		switch (stepExecution.getExitStatus().getExitCode()) {
		case "COMPLETED":
			logger.info("<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");
			logger.info("<< Fin execution Step {} At {} status {} duration {} seconds", stepExecution.getId(),
					Instant.now(), stepExecution.getExitStatus(), watch.getTotalTimeSeconds());
			logger.info("<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");
			break;
		default:
			logger.error("<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");
			logger.error("<< Fin execution Step {} At {} status {} duration {} seconds", stepExecution.getId(),
					Instant.now(), stepExecution.getExitStatus(), watch.getTotalTimeSeconds());
			logger.error("<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");
			break;
		}
		return stepExecution.getExitStatus();
	}

}
