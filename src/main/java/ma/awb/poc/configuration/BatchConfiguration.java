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
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
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
import org.springframework.transaction.annotation.EnableTransactionManagement;

import ma.awb.poc.batch.listener.JobListener;
import ma.awb.poc.batch.listener.StepListener;
import ma.awb.poc.batch.partitioner.PocPartitioner;
import ma.awb.poc.batch.tasklet.ArchiveTasklet;
import ma.awb.poc.batch.writer.EventBatchItemWriter;
import ma.awb.poc.core.dao.vo.EventBatchVO;

@Import(value = { PersistenceConfiguration.class })
@EnableBatchProcessing
@EnableTransactionManagement
@EnableAutoConfiguration
@Configuration
public class BatchConfiguration {

	@Value("${poc.reader.maxResults}")
	private int maxResult;
	@Value("${poc.reader.chunck}")
	private int chunck;
	@Value("${poc.poolThread.corePoolSize}")
	private int corePoolSize;
	@Value("${poc.poolThread.maxPoolSize}")
	private int maxPoolSize;
	@Value("${poc.poolThread.queueCapacity}")
	private int queueCapacity;
	@Value("${poc.partitioner.gridSize}")
	private int gridSize;

	@Qualifier("dataSourceBatch")
	@Autowired
	private DataSource dataSourceBatch;

	@Autowired
	private LocalContainerEntityManagerFactoryBean entityManager;

	@Autowired
	private StepBuilderFactory stepBuilderFactory;

	@Autowired
	private JobBuilderFactory jobBuilderFactory;

	@Bean
	public BatchConfigurer batchConfigurer() {
		return new DefaultBatchConfigurer(dataSourceBatch) {
			@Override
			protected JobRepository createJobRepository() throws Exception {
				JobRepositoryFactoryBean jobRepositoryFactoryBean = new JobRepositoryFactoryBean();
				jobRepositoryFactoryBean.setDataSource(dataSourceBatch);
				jobRepositoryFactoryBean.setTransactionManager(getResourcelessTransactionManager());
				jobRepositoryFactoryBean.afterPropertiesSet();
				return jobRepositoryFactoryBean.getObject();
			}
		};
	}

	@Bean
	protected JobLauncher jobLauncher() throws Exception {
		SimpleJobLauncher jobLauncher = new SimpleJobLauncher();
		jobLauncher.setJobRepository(jobRepository());
		jobLauncher.setTaskExecutor(taskExecutor());
		jobLauncher.afterPropertiesSet();
		return jobLauncher;
	}

	public ResourcelessTransactionManager getResourcelessTransactionManager() {
		return new ResourcelessTransactionManager();
	}

	@Bean
	public JobRepository jobRepository() throws Exception {
		JobRepositoryFactoryBean jobRepositoryFactoryBean = new JobRepositoryFactoryBean();
		jobRepositoryFactoryBean.setDataSource(dataSourceBatch);
		jobRepositoryFactoryBean.setTransactionManager(getResourcelessTransactionManager());
		jobRepositoryFactoryBean.afterPropertiesSet();
		return jobRepositoryFactoryBean.getObject();
	}

	@Bean
	public TaskExecutor taskExecutor() {
		ThreadPoolTaskExecutor threadPoolTaskExecutor = new ThreadPoolTaskExecutor();
		threadPoolTaskExecutor.setCorePoolSize(corePoolSize);
		threadPoolTaskExecutor.setMaxPoolSize(maxPoolSize);
		threadPoolTaskExecutor.setQueueCapacity(queueCapacity);
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

	@StepScope
	@Bean
	public JpaPagingItemReader<EventBatchVO> itemRader(@Value("#{stepExecutionContext['criterion']}") String criterion)
			throws Exception {
		JpaPagingItemReader<EventBatchVO> jpaPagingItemReaderBuilder = new JpaPagingItemReaderBuilder<EventBatchVO>()
				.pageSize(chunck).name("itemRader").entityManagerFactory(entityManager.getObject())
				.queryString("select u from EventBatchVO u where u.treated is null  and " + criterion).saveState(true)
				.transacted(true).maxItemCount(maxResult).build();
		return jpaPagingItemReaderBuilder;
	}

	@StepScope
	@Bean
	public ItemWriter<EventBatchVO> itemWriter(@Value("#{stepExecutionContext['file']}") String file) {
		EventBatchItemWriter itemWriter = new EventBatchItemWriter(file);
		return itemWriter;
	}

	@Bean
	public Step stepSlave() throws Exception {
		return stepBuilderFactory.get("stepSlave").listener(stepExecutionListener())
				.<EventBatchVO, EventBatchVO>chunk(chunck).reader(itemRader(null)).writer(itemWriter(null)).build();
	}

	@Bean
	public Step stepMaster() throws Exception {
		return stepBuilderFactory.get("stepMaster").listener(stepExecutionListener())
				.partitioner(stepSlave().getName(), new PocPartitioner()).step(stepSlave()).gridSize(gridSize)
				.taskExecutor(taskExecutor()).build();
	}

	@Primary
	@Bean(name = "jobPoc")
	public Job jobPoc() throws Exception {
		return jobBuilderFactory.get("jobPoc").listener(jobExecutionListenerSupport()).repository(jobRepository())
				.flow(stepMaster()).next(stepBuilderFactory.get("archiveStep").tasklet(tasklet()).build()).end()
				.build();
	}
}
