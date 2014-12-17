package ufo.remote.calls.benchmark.client;

import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import ufo.remote.calls.benchmark.client.Config;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Config.class)   // 2
public abstract class BenchmarkBaseTest {

	protected Logger logger = LoggerFactory.getLogger(getClass());

}
