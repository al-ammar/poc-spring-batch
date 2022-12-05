package ma.awb.poc.batch.listener;

import static ma.awb.poc.core.util.DateUtil.duration;
import static ma.awb.poc.core.util.DateUtil.format;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.listener.StepExecutionListenerSupport;

public class StepListener extends StepExecutionListenerSupport {

	private static final Logger logger = LoggerFactory.getLogger(StepListener.class);

	private Date dateDebut;

	@Override
	public void beforeStep(StepExecution stepExecution) {
		this.dateDebut = new Date();
		logger.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
		logger.info(">> Début Step {} At {}", stepExecution.getStepName(), format(this.dateDebut));
		logger.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
	}

	@Override
	public ExitStatus afterStep(StepExecution stepExecution) {
		Date dateFin = new Date();
		switch (stepExecution.getExitStatus().getExitCode()) {
		case "COMPLETED":
			logger.info("<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");
			logger.info("<< Fin execution Step {} At {} status {} duration {} seconds", stepExecution.getStepName(),
					format(dateFin), stepExecution.getExitStatus().getExitCode(), duration(dateDebut, dateFin));
			logger.info("<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");
			break;
		default:
			logger.error("<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");
			logger.error("<< Failure execution Step {} At {} status {} duration {} seconds",
					stepExecution.getStepName(), format(dateFin), stepExecution.getExitStatus().getExitCode(),
					duration(dateDebut, dateFin));
			logger.error("<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");
			break;
		}
		return stepExecution.getExitStatus();
	}

}
