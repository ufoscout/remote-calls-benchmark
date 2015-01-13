package ufo.remote.calls.benchmark.server.vertx.cluster;

import io.vertx.test.core.VertxTestBase;

import org.junit.Test;

import com.hazelcast.config.Config;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;

public class ClusterConfigTest extends VertxTestBase {

	//private final Logger logger = LoggerFactory.getLogger(getClass());

	@Test
	public void testHazelcastClusterConfig() throws InterruptedException {
		Config cfg = new Config();
		//        NetworkConfig network = cfg.getNetworkConfig();
		//        network.setPort(PORT_NUMBER);
		//
		//        JoinConfig join = network.getJoin();
		//        join.getTcpIpConfig().setEnabled(false);
		//        join.getAwsConfig().setEnabled(false);
		//        join.getMulticastConfig().setEnabled(true);
		//
		//        join.getMulticastConfig().setMulticastGroup(MULTICAST_ADDRESS);
		//        join.getMulticastConfig().setMulticastPort(PORT_NUMBER);
		//        join.getMulticastConfig().setMulticastTimeoutSeconds(200);

		HazelcastInstance instance1 = Hazelcast.newHazelcastInstance(cfg);
		assertEquals(1, instance1.getCluster().getMembers().size());

		HazelcastInstance instance2 = Hazelcast.newHazelcastInstance(cfg);

		assertEquals(2, instance1.getCluster().getMembers().size());
		assertEquals(2, instance2.getCluster().getMembers().size());
	}

}
