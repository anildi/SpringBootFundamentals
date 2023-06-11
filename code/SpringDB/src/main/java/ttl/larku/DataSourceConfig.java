package ttl.larku;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.vendor.Database;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;

//Here is a case where SpringBoot is mysteriously doing the wrong thing.
//If we do not exclude the DataSourceAutoConfiguration class, we get
//duplicate bean exceptions because it seems to make an instance of
//a DataSourceProperties object even though we have our own.
//Try commenting this next line out and see what happens.
//@EnableAutoConfiguration(exclude={DataSourceAutoConfiguration.class})
//Edit: the above works, *except* when you are using h2 for some
//as yet unknown reason.  So back to using @Primary.

//@Configuration
@Profile("production")
public class DataSourceConfig {

    @Primary
    @Profile("derby")
    @Bean
    @ConfigurationProperties("derbydb.datasource")
    public DataSourceProperties derbyDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Profile("derby")
    @Bean
    @ConfigurationProperties("derbydb.datasource.configuration")
    public HikariDataSource derbyDataSource() {
        return derbyDataSourceProperties().initializeDataSourceBuilder()
                .type(HikariDataSource.class).build();
    }

    @Primary
    @Profile("mysql")
    @Bean
    @ConfigurationProperties("mysqldb.datasource")
    public DataSourceProperties mysqlDatasourceProperties() {
        return new DataSourceProperties();
    }


    @Profile("mysql")
    @Bean
    @ConfigurationProperties("mysqldb.datasource.configuration")
    public HikariDataSource mysqlDataSource() {
        return mysqlDatasourceProperties().initializeDataSourceBuilder()
                .type(HikariDataSource.class).build();
    }


    @Primary
    @Profile("h2")
    @Bean
    @ConfigurationProperties("h2.datasource")
    public DataSourceProperties h2DataSourceProperties() {
        DataSourceProperties dsp = new DataSourceProperties();
        return dsp;
    }

    @Profile("h2")
    @Bean
    @ConfigurationProperties("h2.datasource.configuration")
    public HikariDataSource h2DataSource() {
        HikariDataSource hds = h2DataSourceProperties().initializeDataSourceBuilder()
                .type(HikariDataSource.class).build();
        return hds;
    }

    @Bean
    public JpaVendorAdapter jpaVendorAdapter() {
        HibernateJpaVendorAdapter hibernateJpaVendorAdapter = new HibernateJpaVendorAdapter();
        hibernateJpaVendorAdapter.setShowSql(false);
        hibernateJpaVendorAdapter.setGenerateDdl(false);
        hibernateJpaVendorAdapter.setDatabase(Database.MYSQL);
        return hibernateJpaVendorAdapter;
    }
}
