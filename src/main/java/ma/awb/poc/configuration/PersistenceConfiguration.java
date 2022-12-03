package ma.awb.poc.configuration;

import javax.sql.DataSource;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.zaxxer.hikari.HikariDataSource;

@EnableAutoConfiguration
@EnableJpaRepositories(basePackages = { "ma.awb.poc.core.dao.repository" })
@EnableTransactionManagement
@Configuration
public class PersistenceConfiguration {

	private static final String DATABASE_PLATFORM_POSTGRES = "org.hibernate.dialect.PostgreSQL82Dialect";
	private static final String PERSISTENCE_XML_LOCATION = "classpath:META-INF/persistence.xml";
	private static final String PERSISTENCE_UNIT_NAME = "POC_PERSITANCE_UNIT";

	@Primary
	@Bean
	@ConfigurationProperties(prefix = "spring.datasource")
	public DataSource dataSource() {
		return DataSourceBuilder.create().type(HikariDataSource.class).build();
	}

	@Bean
	@ConfigurationProperties(prefix = "spring.datasource.secondary")
	public DataSource dataSourceBatch() {
//		return new EmbeddedDatabaseBuilder().setType(EmbeddedDatabaseType.H2)
//				.addScript("/org/springframework/batch/core/schema-drop-h2.sql")
//				.addScript("/org/springframework/batch/core/schema-h2.sql").build();
		return DataSourceBuilder.create().type(HikariDataSource.class).build();
	}

	@Bean
	public JdbcTemplate jdbcTemplate() {
		JdbcTemplate jdbcTemplate = new JdbcTemplate();
		jdbcTemplate.setDataSource(dataSource());
		jdbcTemplate.afterPropertiesSet();
		return jdbcTemplate;
	}

	@Primary
	@Bean
	public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
		LocalContainerEntityManagerFactoryBean entityManager = new LocalContainerEntityManagerFactoryBean();
		entityManager.setDataSource(dataSource());
		entityManager.setJpaVendorAdapter(jpaVendorAdapter());
		entityManager.setPackagesToScan("ma.awb.poc.core.dao.vo");
		entityManager.setPersistenceUnitName(PERSISTENCE_UNIT_NAME);
		entityManager.setPersistenceXmlLocation(PERSISTENCE_XML_LOCATION);
		return entityManager;
	}

	@Bean
	public JpaVendorAdapter jpaVendorAdapter() {
		HibernateJpaVendorAdapter adapter = new HibernateJpaVendorAdapter();
		adapter.setDatabasePlatform(DATABASE_PLATFORM_POSTGRES);
		adapter.setShowSql(false);
		return adapter;
	}

	/**
	 * Transaction manager
	 * 
	 * @return
	 */
	@Bean
	public PlatformTransactionManager transactionManager() {
		final JpaTransactionManager jpaTransactionManager = new JpaTransactionManager();
		jpaTransactionManager.setDataSource(dataSource());
		jpaTransactionManager.setEntityManagerFactory(entityManagerFactory().getObject());
		jpaTransactionManager.setPersistenceUnitName(PERSISTENCE_UNIT_NAME);
		return jpaTransactionManager;
	}
}
