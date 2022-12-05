package ma.awb.poc.batch.listener;

import java.time.Duration;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.listener.JobExecutionListenerSupport;
import org.springframework.batch.core.metrics.BatchMetrics;

import ma.awb.poc.core.util.DateUtil;

public class JobListener extends JobExecutionListenerSupport {

	private static final Logger logger = LoggerFactory.getLogger(JobListener.class);

	private Date dateDebut;

	@Override
	public void beforeJob(JobExecution jobExecution) {
		dateDebut = new Date();
		logger.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
		logger.info(">> Début execution Job {} At {}", jobExecution.getJobInstance().getJobName(),
				DateUtil.format(dateDebut));
		logger.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
	}

	@Override
	public void afterJob(JobExecution jobExecution) {
		final Date dateFin = new Date();
		Duration duree = BatchMetrics.calculateDuration(dateDebut, dateFin);
		switch (jobExecution.getExitStatus().getExitCode()) {
		case "COMPLETED":
			logger.info("<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");
			logger.info("<< Fin execution Job {} At {} status {} duration {} seconds",
					jobExecution.getJobInstance().getJobName(), DateUtil.format(dateFin),
					jobExecution.getExitStatus().getExitCode(), BatchMetrics.formatDuration(duree));
			logger.info("<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");
			break;
		default:
			logger.error("<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");
			logger.error("<< Failure execution Job {} At {} status {} duration {} seconds",
					jobExecution.getJobInstance().getJobName(), DateUtil.format(dateFin),
					jobExecution.getExitStatus().getExitCode(), BatchMetrics.formatDuration(duree));
			logger.error("<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");
			break;
		}

	}
}
