package ma.awb.poc.batch.listener;

import java.time.Duration;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.listener.StepExecutionListenerSupport;
import org.springframework.batch.core.metrics.BatchMetrics;

import ma.awb.poc.core.util.DateUtil;

public class StepListener extends StepExecutionListenerSupport {

	private static final Logger logger = LoggerFactory.getLogger(StepListener.class);

	private Date dateDebut;

	@Override
	public void beforeStep(StepExecution stepExecution) {
		this.dateDebut = new Date();
		logger.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
		logger.info(">> Début Step {} At {}", stepExecution.getStepName(), DateUtil.format(this.dateDebut));
		logger.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
	}

	@Override
	public ExitStatus afterStep(StepExecution stepExecution) {
		Date dateFin = new Date();
		Duration stepExecutionDuration = BatchMetrics.calculateDuration(dateDebut, dateFin);
		switch (stepExecution.getExitStatus().getExitCode()) {
		case "COMPLETED":
			logger.info("<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");
			logger.info("<< Fin execution Step {} At {} status {} duration {} seconds", stepExecution.getStepName(),
					DateUtil.format(dateFin), stepExecution.getExitStatus().getExitCode(),
					BatchMetrics.formatDuration(stepExecutionDuration));
			logger.info("<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");
			break;
		default:
			logger.error("<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");
			logger.error("<< Failure execution Step {} At {} status {} duration {} seconds", stepExecution.getStepName(),
					DateUtil.format(dateFin), stepExecution.getExitStatus().getExitCode(),
					BatchMetrics.formatDuration(stepExecutionDuration));
			logger.error("<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");
			break;
		}
		return stepExecution.getExitStatus();
	}

}
