package ufo.remote.calls.benchmark;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import ufo.remote.calls.benchmark.client.Benchmark;

@Configuration
@EnableAutoConfiguration
@ComponentScan
public class Application {

	@Autowired
	private ConfigurableApplicationContext context;
	@Autowired
	private Benchmark benchmark;

	public static void main(final String[] args) {
		SpringApplication.run(Application.class, args).getBean(Benchmark.class).start();
		System.exit(0);
	}

}
