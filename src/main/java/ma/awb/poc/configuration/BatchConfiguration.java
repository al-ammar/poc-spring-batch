package ma.awb.poc.configuration;

import javax.sql.DataSource;

import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.support.JobRepositoryFactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.PlatformTransactionManager;

@Import(value = { PersistenceConfiguration.class })
@EnableBatchProcessing
@EnableAutoConfiguration
@Configuration
public class BatchConfiguration {

	@Autowired
	private DataSource dataSource;

	@Autowired
	@Qualifier("transactionManager")
	private PlatformTransactionManager transactionManager;

	@Bean
	public JobRepository jobRepository() throws Exception {
		JobRepositoryFactoryBean jobRepositoryFactoryBean = new JobRepositoryFactoryBean();
		jobRepositoryFactoryBean.setDataSource(dataSource);
		jobRepositoryFactoryBean.setTransactionManager(transactionManager);
		return jobRepositoryFactoryBean.getObject();
	}

// TODO
//	@Bean
//	public JobExecutionListenerSupport jobExecutionListenerSupport() {
//		return new JobExecutionListenerSupport();
//	}

	// TODO
//	@Bean
//	public Job job() throws Exception {
//		JobBuilderFactory factory = new JobBuilderFactory(jobRepository());
//		return factory.get("JOB_POC").incrementer(new RunIdIncrementer()).flow(null).end().build();
//	}

	// TODO
//	@Bean
//	public Step step() throws Exception {
//		StepBuilderFactory factory = new StepBuilderFactory(jobRepository(), transactionManager);
//		return factory.get("STEP").job(null).build();
//	}

}
