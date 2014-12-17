package ufo.remote.calls.benchmark;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import ufo.remote.calls.benchmark.client.EchoBenchmarkService;

@Configuration
@EnableAutoConfiguration
@ComponentScan
public class Application {

	@Autowired
	private EchoBenchmarkService echoBenchmarkService;
	@Autowired
	private ConfigurableApplicationContext context;

	public static void main(final String[] args) {
		SpringApplication.run(Application.class, args);
	}

	@PostConstruct
	public void init() {
		String remoteUrl = "http://localhost:8180/test/echo/helloGet";
		int parallelThreads = 50;
		int callsPerThread = 100;

		echoBenchmarkService.testEcho(remoteUrl, parallelThreads, callsPerThread);

		//System.exit(0);
		//context.close();
	}
}
