package ma.awb.poc.configuration;

import java.util.Properties;

import javax.sql.DataSource;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.BatchConfigurer;
import org.springframework.batch.core.configuration.annotation.DefaultBatchConfigurer;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.support.SimpleJobLauncher;
import org.springframework.batch.core.listener.JobExecutionListenerSupport;
import org.springframework.batch.core.listener.StepExecutionListenerSupport;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.support.JobRepositoryFactoryBean;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.interceptor.TransactionProxyFactoryBean;

import ma.awb.poc.batch.listener.JobListener;
import ma.awb.poc.batch.listener.StepListener;
import ma.awb.poc.batch.process.UserItemProcessor;
import ma.awb.poc.batch.tasklet.ArchiveTasklet;
import ma.awb.poc.batch.writer.UserItemWriter;
import ma.awb.poc.core.dao.vo.UserVO;
import ma.awb.poc.core.model.UserDTO;

@Import(value = { PersistenceConfiguration.class })
@EnableAutoConfiguration
@EnableBatchProcessing
@Configuration
public class BatchConfiguration {

	private static final String STEP_NAME = "STEP_POC";
	private static final String JOB_NAME = "JOB_POC";

	@Qualifier("dataSourceBatch")
	@Autowired
	private DataSource dataSourceBatch;

	@Autowired
	@Qualifier("transactionManager")
	private PlatformTransactionManager transactionManager;

	@Autowired
	private LocalContainerEntityManagerFactoryBean entityManager;

	@Bean
	public TransactionProxyFactoryBean baseProxy() throws Exception {
		TransactionProxyFactoryBean factoryBean = new TransactionProxyFactoryBean();
		final Properties props = new Properties();
		props.setProperty("*", "PROPAGATION_REQUIRED");
		factoryBean.setTransactionAttributes(props);
		factoryBean.setTarget(jobRepository());
		factoryBean.setTransactionManager(transactionManager);
		return factoryBean;
	}

	@Bean
	public BatchConfigurer batchConfigurer() {
		return new DefaultBatchConfigurer(dataSourceBatch);
	}

	@Bean
	protected JobLauncher createJobLauncher() throws Exception {
		SimpleJobLauncher jobLauncher = new SimpleJobLauncher();
		jobLauncher.setJobRepository(jobRepository());
//		jobLauncher.setTaskExecutor(taskExecutor());
		jobLauncher.afterPropertiesSet();
		return jobLauncher;
	}

	@Bean
	public JobRepository jobRepository() throws Exception {
		JobRepositoryFactoryBean jobRepositoryFactoryBean = new JobRepositoryFactoryBean();
		jobRepositoryFactoryBean.setDataSource(dataSourceBatch);
		jobRepositoryFactoryBean.setTransactionManager(transactionManager);
		jobRepositoryFactoryBean.afterPropertiesSet();
		return jobRepositoryFactoryBean.getObject();
	}

//	@Bean
//	public TaskExecutor taskExecutor() {
//		ThreadPoolTaskExecutor threadPoolTaskExecutor = new ThreadPoolTaskExecutor();
//		threadPoolTaskExecutor.setCorePoolSize(2);
//		threadPoolTaskExecutor.setMaxPoolSize(1);
//		threadPoolTaskExecutor.afterPropertiesSet();
//		return threadPoolTaskExecutor;
//	}

	@Bean
	public JobExecutionListenerSupport jobExecutionListenerSupport() {
		return new JobListener();
	}

	@Bean
	public StepExecutionListenerSupport stepExecutionListener() {
		return new StepListener();
	}

	@Bean
	public StepBuilderFactory stepBuilderFactory() throws Exception {
		return new StepBuilderFactory(jobRepository(), transactionManager);
	}

	@Bean
	public JobBuilderFactory jobBuilderFactory() throws Exception {
		return new JobBuilderFactory(jobRepository());
	}

	@Bean
	public JpaPagingItemReader<UserVO> itemRader() {
		JpaPagingItemReader<UserVO> jpaPagingItemReaderBuilder = new JpaPagingItemReaderBuilder<UserVO>()
				.name("PocReader").entityManagerFactory(entityManager.getObject()).queryString("select u from UserVO u")
				.maxItemCount(200).build();
		// Make ItemReader Thread-safe
//		SynchronizedItemStreamReader<UserVO> itemStreamReader = new SynchronizedItemStreamReader<UserVO>();
//		itemStreamReader.setDelegate(jpaPagingItemReaderBuilder);
		return jpaPagingItemReaderBuilder;
	}

	@Bean
	public ItemProcessor<UserVO, UserDTO> itemProcessor() {
		return new UserItemProcessor();
	}

	@Bean
	public ItemWriter<UserDTO> itemWriter() {
		UserItemWriter itemWriter = new UserItemWriter();
//		SynchronizedItemStreamWriter<UserDTO> itemStreamWriter = new SynchronizedItemStreamWriter<UserDTO>();
//		itemStreamWriter.setDelegate(itemStreamWriter);
		return itemWriter;
	}

	@Bean
	public ArchiveTasklet tasklet() {
		return new ArchiveTasklet();
	}

	@Bean
	public Job job() throws Exception {
		return jobBuilderFactory().get(JOB_NAME)
//				.incrementer(new RunIdIncrementer())
				.flow(step()).next(stepBuilderFactory().get("archiveStep").tasklet(tasklet()).build()).end().build();
	}

	@Bean
	public Step step() throws Exception {
		return stepBuilderFactory().get(STEP_NAME).listener(stepExecutionListener()).<UserVO, UserDTO>chunk(100)
				.reader(itemRader()).processor(itemProcessor()).writer(itemWriter())
//				.taskExecutor(taskExecutor())
//				.throttleLimit(2)
				.build();
	}
}
