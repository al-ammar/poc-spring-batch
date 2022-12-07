package ma.awb.poc.batch.listener;

import static ma.awb.poc.core.util.DateUtil.duration;
import static ma.awb.poc.core.util.DateUtil.format;

import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.listener.StepExecutionListenerSupport;

public class StepListener extends StepExecutionListenerSupport {

	private static final Logger logger = LoggerFactory.getLogger(StepListener.class);
	public static final String LINE = "<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<";

	private Date dateDebut;

	@Override
	public void beforeStep(StepExecution stepExecution) {
		this.dateDebut = new Date();
		final String debut = format(this.dateDebut);
		logger.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
		logger.info(">> Début Step {} At {}", stepExecution.getStepName(), debut);
		logger.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
	}

	@Override
	public ExitStatus afterStep(final StepExecution stepExecution) {
		final Date dateFin = new Date();
		final String fin = format(dateFin);
		final String duration = duration(dateDebut, dateFin);
		if (StringUtils.equalsIgnoreCase(stepExecution.getExitStatus().getExitCode(), "COMPLETED")) {
			logger.info(LINE);
			logger.info("<< Fin execution Step {} At {} status {} duration {} seconds", stepExecution.getStepName(),
					fin, stepExecution.getExitStatus().getExitCode(), duration);
			logger.info(LINE);

		} else {
			logger.error(LINE);
			logger.error("<< Failure execution Step {} At {} status {} duration {} seconds",
					stepExecution.getStepName(), fin, stepExecution.getExitStatus().getExitCode(), duration);
			logger.error(LINE);
		}
		return stepExecution.getExitStatus();
	}

}
