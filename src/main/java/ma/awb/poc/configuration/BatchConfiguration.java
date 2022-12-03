package ma.awb.poc.configuration;

import javax.sql.DataSource;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.BatchConfigurer;
import org.springframework.batch.core.configuration.annotation.DefaultBatchConfigurer;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
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
import org.springframework.batch.item.support.SynchronizedItemStreamReader;
import org.springframework.batch.item.support.SynchronizedItemStreamWriter;
import org.springframework.batch.support.transaction.ResourcelessTransactionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.core.task.TaskExecutor;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import ma.awb.poc.batch.listener.JobListener;
import ma.awb.poc.batch.listener.StepListener;
import ma.awb.poc.batch.partitioner.PocPartitioner;
import ma.awb.poc.batch.process.UserItemProcessor;
import ma.awb.poc.batch.tasklet.ArchiveTasklet;
import ma.awb.poc.batch.writer.UserItemWriter;
import ma.awb.poc.core.dao.vo.UserVO;
import ma.awb.poc.core.model.UserDTO;

@Import(value = { PersistenceConfiguration.class })
@EnableBatchProcessing
@EnableTransactionManagement
@EnableAutoConfiguration
@Configuration
public class BatchConfiguration {

	private static final String STEP_NAME = "STEP_POC";
	private static final String JOB_NAME = "JOB_POC";

	@Value("${poc.reader.maxResults}")
	private int maxResult;

	@Value("${poc.reader.chunck}")
	private int chunck;

	@Qualifier("dataSourceBatch")
	@Autowired
	private DataSource dataSourceBatch;

	@Autowired
	private LocalContainerEntityManagerFactoryBean entityManager;

	@Autowired
	private StepBuilderFactory stepBuilderFactory;

	@Autowired
	private JobBuilderFactory jobBuilderFactory;

	@Autowired
	private PlatformTransactionManager transactionManager;

//	@Bean
//	public TransactionProxyFactoryBean baseProxy() throws Exception {
//		TransactionProxyFactoryBean factoryBean = new TransactionProxyFactoryBean();
//		final Properties props = new Properties();
//		props.setProperty("*", "PROPAGATION_REQUIRED");
//		factoryBean.setTransactionAttributes(props);
//		factoryBean.setTarget(jobRepository());
//		factoryBean.setTransactionManager(transactionManager);
//		return factoryBean;
//	}

	@Bean
	public BatchConfigurer batchConfigurer() {
		return new DefaultBatchConfigurer(dataSourceBatch);
//		{
//			@Override
//			protected JobRepository createJobRepository() throws Exception {
//				JobRepositoryFactoryBean jobRepositoryFactoryBean = new JobRepositoryFactoryBean();
//				jobRepositoryFactoryBean.setDataSource(dataSourceBatch);
//				jobRepositoryFactoryBean.setTransactionManager(getTransactionManager());
//				jobRepositoryFactoryBean.afterPropertiesSet();
//				return jobRepositoryFactoryBean.getObject();
//			}
//
//			@Override
//			protected JobLauncher createJobLauncher() throws Exception {
//				SimpleJobLauncher jobLauncher = new SimpleJobLauncher();
//				jobLauncher.setJobRepository(createJobRepository());
//				jobLauncher.setTaskExecutor(taskExecutor());
//				jobLauncher.afterPropertiesSet();
//				return jobLauncher;
//			}
//
//		};
	}

//	public JobLauncher getJobLauncher() throws Exception {
//		SimpleJobLauncher jobLauncher = new SimpleJobLauncher();
//		// SimpleJobLauncher's methods Throws Generic Exception,
//		// it would have been better to have a specific one
//		jobLauncher.setJobRepository(getJobRepository());
//		jobLauncher.afterPropertiesSet();
//		return jobLauncher;
//	}
//
//	private JobRepository getJobRepository() throws Exception {
//		JobRepositoryFactoryBean factory = new JobRepositoryFactoryBean();
//		factory.setDataSource(dataSourceBatch);
//		factory.setTransactionManager(getTransactionManager());
//		// JobRepositoryFactoryBean's methods Throws Generic Exception,
//		// it would have been better to have a specific one
//		factory.afterPropertiesSet();
//		return factory.getObject();
//	}
//
//	private PlatformTransactionManager getTransactionManager() {
//		return new ResourcelessTransactionManager();
//	}

	@Bean
	protected JobLauncher createJobLauncher() throws Exception {
		SimpleJobLauncher jobLauncher = new SimpleJobLauncher();
		jobLauncher.setJobRepository(jobRepository());
		jobLauncher.setTaskExecutor(taskExecutor());
		jobLauncher.afterPropertiesSet();
		return jobLauncher;
	}

	@Bean
	public JobRepository jobRepository() throws Exception {
		JobRepositoryFactoryBean jobRepositoryFactoryBean = new JobRepositoryFactoryBean();
		jobRepositoryFactoryBean.setDataSource(dataSourceBatch);
		jobRepositoryFactoryBean.setTransactionManager(new ResourcelessTransactionManager());
		jobRepositoryFactoryBean.afterPropertiesSet();
		return jobRepositoryFactoryBean.getObject();
	}

	@Bean
	public TaskExecutor taskExecutor() {
		ThreadPoolTaskExecutor threadPoolTaskExecutor = new ThreadPoolTaskExecutor();
		threadPoolTaskExecutor.setCorePoolSize(20);
		threadPoolTaskExecutor.setMaxPoolSize(20);
//		threadPoolTaskExecutor.setQueueCapacity(20);
		threadPoolTaskExecutor.afterPropertiesSet();
		return threadPoolTaskExecutor;
	}

	@Bean
	public JobExecutionListenerSupport jobExecutionListenerSupport() {
		return new JobListener();
	}

	@Bean
	public StepExecutionListenerSupport stepExecutionListener() {
		return new StepListener();
	}

	@Bean
	public ArchiveTasklet tasklet() {
		return new ArchiveTasklet();
	}

//	@Bean
//	public Job job() throws Exception {
//		return jobBuilderFactory().get(JOB_NAME).incrementer(new RunIdIncrementer())
//				.listener(jobExecutionListenerSupport()).flow(step())
//				.next(stepBuilderFactory().get("archiveStep").tasklet(tasklet()).build()).end().build();
//	}

//	@Bean(name = "partitionerJob")
//	public Job partitionerJob() throws Exception {
//		return jobBuilderFactory.get("partitionerJob")
//				.incrementer(new RunIdIncrementer())
//				.start(partitionerStep())
//				.build();
//	}
//
//	@Bean
//	public Step partitionerStep() throws Exception {
//		return stepBuilderFactory.get("partitionerStep")
//				.partitioner("slaveStep1", partitioner())
//				.step(slaveStep())
//				.taskExecutor(taskExecutor())
//				.gridSize(20)
//				.build();
//	}
//
//	@Bean
//	public Step slaveStep() throws Exception {
//		return stepBuilderFactory.get("slaveStep")
////				.listener(stepExecutionListener())
//				.<UserVO, UserDTO>chunk(chunck)
//				.reader(itemRader(null))
////				.processor(itemProcessor())
//				.writer(itemWriter())
////				.taskExecutor(taskExecutor())
////				.throttleLimit(5)
//				.build();
//	}

	@StepScope
	@Bean
	public JpaPagingItemReader<UserVO> itemRader(
			@Value("#{stepExecutionContext['current']}") 
			Integer current
	) throws Exception {
		JpaPagingItemReader<UserVO> jpaPagingItemReaderBuilder = new JpaPagingItemReaderBuilder<UserVO>()
				.currentItemCount(current)
				.name("PocReader").entityManagerFactory(entityManager.getObject()).queryString("select u from UserVO u")
				.saveState(false).maxItemCount(maxResult).build();
		// Make ItemReader Thread-safe
		SynchronizedItemStreamReader<UserVO> itemStreamReader = new SynchronizedItemStreamReader<UserVO>();
		itemStreamReader.setDelegate(jpaPagingItemReaderBuilder);
//		jpaPagingItemReaderBuilder.afterPropertiesSet();
		return jpaPagingItemReaderBuilder;
	}

//	@StepScope
	@Bean
	public ItemProcessor<UserVO, UserDTO> itemProcessor() {
		return new UserItemProcessor();
	}

//	@StepScope
	@Bean
	public ItemWriter<UserVO> itemWriter() {
		UserItemWriter itemWriter = new UserItemWriter();
		SynchronizedItemStreamWriter<UserDTO> itemStreamWriter = new SynchronizedItemStreamWriter<UserDTO>();
		itemStreamWriter.setDelegate(itemStreamWriter);
		return itemWriter;
	}

	@Bean
	public Step stepSlave() throws Exception {
		return stepBuilderFactory.get("stepSlave").listener(stepExecutionListener()).<UserVO, UserVO>chunk(chunck)
				.reader(itemRader(null))
//				.processor(itemProcessor())
				.writer(itemWriter())
//				.taskExecutor(taskExecutor())
//				.throttleLimit(5)
				.build();
	}

	@Bean
	public Step stepMaster() throws Exception {
		return stepBuilderFactory.get("stepMaster")
				.partitioner(stepSlave().getName(), new PocPartitioner(maxResult, chunck))
//				.partitionHandler(partitionerHandler())
				.step(stepSlave()).gridSize(4).taskExecutor(taskExecutor()).build();
	}

	@Primary
	@Bean(name = "jobPoc")
	public Job jobPoc() throws Exception {
		return jobBuilderFactory.get("jobPoc").repository(jobRepository())
//				.incrementer(new RunIdIncrementer())
//				.start(stepMaster())

				.flow(stepMaster()).next(stepBuilderFactory.get("archiveStep").tasklet(tasklet()).build()).end()

				// .next(stepBuilderFactory.get("archiveStep").tasklet(tasklet())
//				.build())
//				.end()
				.build();
	}

	/*
	 * @Bean public PocPartitioner partitioner() { PocPartitioner partitioner = new
	 * PocPartitioner(maxResult, chunck); return partitioner; }
	 */

//	@Bean
//	public PartitionHandler partitionerHandler() throws Exception {
//		TaskExecutorPartitionHandler handler = new TaskExecutorPartitionHandler();
//		handler.setGridSize(10);
//		handler.setTaskExecutor(taskExecutor());
//		handler.setStep(stepSlave());
//		return handler;
//	}
}
