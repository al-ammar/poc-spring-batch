package ma.awb.poc.configuration;

import org.springframework.batch.core.configuration.annotation.BatchConfigurer;
import org.springframework.batch.core.configuration.annotation.DefaultBatchConfigurer;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.zaxxer.hikari.HikariDataSource;

import ma.awb.poc.core.dao.vo.UserVO;

@EnableTransactionManagement
@EntityScan(basePackageClasses = UserVO.class)
@Configuration
public class PersistenceConfiguration {

	private static final String DATABASE_PLATFORM_POSTGRES = "org.hibernate.dialect.PostgreSQL82Dialect";

	@Primary
	@Bean
	@ConfigurationProperties(prefix = "spring.datasource")
	public HikariDataSource dataSource() {
		HikariDataSource dataSource = DataSourceBuilder.create().type(HikariDataSource.class).build();
		return dataSource;
	}

	@Bean
	public JdbcTemplate jdbcTemplate() {
		return new JdbcTemplate(dataSource());
	}

	@Bean
	public LocalContainerEntityManagerFactoryBean entityManager() {
		LocalContainerEntityManagerFactoryBean entityManager = new LocalContainerEntityManagerFactoryBean();
		entityManager.setDataSource(dataSource());
		entityManager.setJpaVendorAdapter(jpaVendorAdapter());
		entityManager.setPackagesToScan("ma.awb.poc.core.dao");
		return entityManager;
	}

	@Bean
	public JpaVendorAdapter jpaVendorAdapter() {
		HibernateJpaVendorAdapter adapter = new HibernateJpaVendorAdapter();
		adapter.setDatabasePlatform(DATABASE_PLATFORM_POSTGRES);
		adapter.setShowSql(false);
		return adapter;
	}

	@Bean
	public BatchConfigurer batchConfigurer() {
		return new DefaultBatchConfigurer(dataSource());
	}
}
