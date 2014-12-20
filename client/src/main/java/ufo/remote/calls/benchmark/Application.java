package ufo.remote.calls.benchmark;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import ufo.remote.calls.benchmark.client.caller.Tester;
import ufo.remote.calls.benchmark.client.caller.TesterExecutor;
import ufo.remote.calls.benchmark.client.caller.vertx.VertxHttpClientTester;

@Configuration
@EnableAutoConfiguration
@ComponentScan
public class Application {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	private ConfigurableApplicationContext context;
	@Autowired
	private TesterExecutor testerExecutor;

	public static void main(final String[] args) {
		SpringApplication.run(Application.class, args);
	}

	@PostConstruct
	public void init() {
		int port_spring_boot_tomcat = 8180;
		int port_spring_boot_jetty = 8181;
		//int port_spring_boot_undertow = 8182;
		int port_vertx3_tcp = 8183;
		int port_jetty_async = 8184;

		int totalCalls = 50000;

		//doBenchmark("Spring-Boot-Undertow", port_spring_boot_undertow, parallelThreads, callsPerThread);

		doBenchmark("VERTX3-TCP", totalCalls, new VertxHttpClientTester(port_vertx3_tcp));

		//doBenchmark("JETTY_ASYNC", port_jetty_async, parallelThreads, callsPerThread);

		//doBenchmark("SPRING-BOOT-JETTY", port_spring_boot_jetty, parallelThreads, callsPerThread);

		//doBenchmark("SPRING-BOOT-TOMCAT", port_spring_boot_tomcat, parallelThreads, callsPerThread);

		//System.exit(0);
		//context.close();
	}

	private void doBenchmark(final String serverType, final int calls, final Tester tester) {
		logger.info("---------------------------------------------");

		logger.info("Start [{}] warmup 1", serverType);
		testerExecutor.execute("helloWorld", 100, tester);

		logger.info("Start [{}] warmup 2", serverType);
		testerExecutor.execute("helloWorld", 100, tester);

		logger.info("Start [{}] benchmark 1", serverType);
		testerExecutor.execute("helloWorld", calls, tester);

		logger.info("Start [{}] benchmark 2", serverType);
		testerExecutor.execute("helloWorld", calls, tester);

		logger.info("---------------------------------------------");
	}
}
