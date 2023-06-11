package ttl.larku.service.reg.integcustomcontext.integration;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import ttl.larku.SpringBootApp;

/**
 * An Example of a Test Configuration.
 *
 * If you run this without the 'excludeFilters', the test will run, but it will also pick
 * the SpringBootApp, which will end up making the entire context, i.e. as if you had just
 * said @SpringBootTest to run the test.
 *
 * We could point at individual packages, but we have a bunch of stuff defined in LarkUConfig,
 * which is in the ttl.larku package, so we need to scan from "ttl.larku".  Which
 * means we need the exclude filter to filter out the SpringBootApp class so we only
 * pick up our config files from ttl.larku
 *
 * Also, if we want to use @ConfigurationProperties, then we need to bring in more beans,
 * but exactly which ones is not clear.  At that point, @SpringBootTest(webenvironment = NONE)
 * is probably your best bet.
 */
@Configuration
@EnableConfigurationProperties
//@ComponentScan(basePackages = {"ttl.larku"})
//@ComponentScan(basePackages = {"ttl.larku"}, excludeFilters = @ComponentScan.Filter(type = FilterType.REGEX, pattern = ".*SpringBootApp.*"))
//@ComponentScan(basePackages = {"ttl.larku"}, excludeFilters = @ComponentScan.Filter(type = FilterType.ANNOTATION, classes = SpringBootApplication.class))
@ComponentScan(basePackages = {"ttl.larku"},
        excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE,
        classes = SpringBootApp.class))
public class MyTestConfig{
}
