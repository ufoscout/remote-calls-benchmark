package ufo.remote.calls.benchmark.client;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.AsyncRestTemplate;

@Configuration
@EnableAutoConfiguration
@ComponentScan
public class Config {

	@Bean
	public AsyncRestTemplate getAsyncRestTemplate() {
		return new AsyncRestTemplate();
	}

}
