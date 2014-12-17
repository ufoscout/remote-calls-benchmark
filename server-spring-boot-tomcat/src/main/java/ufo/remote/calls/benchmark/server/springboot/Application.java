package ufo.remote.calls.benchmark.server.springboot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.AsyncRestTemplate;

@Configuration
@EnableAutoConfiguration
@ComponentScan
public class Application {

	public static void main(final String[] args) {
		SpringApplication.run(Application.class, args);
	}

	@Bean
	public AsyncRestTemplate getAsyncRestTemplate() {
		return new AsyncRestTemplate();
	}

	//To add a second servlet
	//	@Bean
	//	public ServletRegistrationBean servletRegistrationBean(){
	//	    return new ServletRegistrationBean(new FooServlet(),"/someOtherUrl/*");
	//	}
}
