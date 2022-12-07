package ma.awb.poc.batch.listener;

import static ma.awb.poc.core.util.DateUtil.duration;
import static ma.awb.poc.core.util.DateUtil.format;

import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.listener.JobExecutionListenerSupport;

public class JobListener extends JobExecutionListenerSupport {

	private static final Logger logger = LoggerFactory.getLogger(JobListener.class);

	private Date dateDebut;

	@Override
	public void beforeJob(JobExecution jobExecution) {
		dateDebut = new Date();
		final String debut = format(dateDebut);
		logger.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
		logger.info(">> Début execution Job {} At {}", jobExecution.getJobInstance().getJobName(), debut);
		logger.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
	}

	@Override
	public void afterJob(JobExecution jobExecution) {
		final Date dateFin = new Date();
		final String fin = format(dateFin);
		final String duration = duration(dateDebut, dateFin);
		if (StringUtils.equalsIgnoreCase(jobExecution.getExitStatus().getExitCode(), "COMPLETED")) {
			logger.info(StepListener.LINE);
			logger.info("<< Fin execution Job {} At {} status {} duration {} seconds",
					jobExecution.getJobInstance().getJobName(), fin, jobExecution.getExitStatus().getExitCode(),
					duration);
			logger.info(StepListener.LINE);
		} else {
			logger.error("<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");
			logger.error("<< Failure execution Job {} At {} status {} duration {} seconds",
					jobExecution.getJobInstance().getJobName(), fin, jobExecution.getExitStatus().getExitCode(),
					duration);
			logger.error("<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");
		}

	}
}
