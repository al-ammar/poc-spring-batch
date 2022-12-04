package ma.awb.poc.batch.listener;

import java.time.Instant;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.listener.StepExecutionListenerSupport;

public class StepListener extends StepExecutionListenerSupport {

	private static final Logger logger = LoggerFactory.getLogger(StepListener.class);

	@Override
	public void beforeStep(StepExecution stepExecution) {
		logger.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
		logger.info(">> Début Step {} At {}", stepExecution.getStepName(), new Date());
		logger.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
	}

	@Override
	public ExitStatus afterStep(StepExecution stepExecution) {

		switch (stepExecution.getExitStatus().getExitCode()) {
		case "COMPLETED":
			logger.info("<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");
			logger.info("<< Fin execution Step {} At {} status {} duration {} seconds", stepExecution.getStepName(),
					new Date(), stepExecution.getExitStatus(), stepExecution.getEndTime());
			logger.info("<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");
			break;
		default:
			logger.error("<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");
			logger.error("<< Fin execution Step {} At {} status {} duration {} seconds", stepExecution.getStepName(),
					new Date(), stepExecution.getExitStatus(), stepExecution.getEndTime());
			logger.error("<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");
			break;
		}
		return stepExecution.getExitStatus();
	}

}
