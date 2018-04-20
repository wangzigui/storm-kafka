package topology;

import java.util.Properties;

import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.StormSubmitter;
import backtype.storm.generated.StormTopology;
import backtype.storm.topology.TopologyBuilder;
import backtype.storm.tuple.Fields;
import bolts.SplitBolt;
import bolts.StaticBolt;
import bolts.WordCounter;
import bolts.WordNormalizer;
import storm.kafka.BrokerHosts;
import storm.kafka.KafkaSpout;
import storm.kafka.SpoutConfig;
import storm.kafka.ZkHosts;
import storm.kafka.trident.TridentKafkaState;

public class StatisticsPVAndUVTopology {

	public static void main(String[] args) throws Exception {
		/**
		 * BrokerHosts hosts ---->storm要连接的kafka的对应的zk列表 String topic
		 * ---->storm要消费的kafka的topic String zkRoot
		 * ---->storm在kafka消费的过程中需要在zk中设置一个工作目录 String id
		 * ---->storm在kafka中消费过程中生产一个标识ID
		 */
//		String zkStr = "10.71.21.212:2181";
		String zkStr = "localhost:2181";
		BrokerHosts hosts = new ZkHosts(zkStr);
		String topic = "my-replicated-topic6";
		String zkRoot = "/storm-kafka-test";
		String id = "storm_kafka_test_id";
		SpoutConfig spoutConf = new SpoutConfig(hosts, topic, zkRoot, id);
//		spoutConf.
//		spoutConf.forceFromStart = true;
		TopologyBuilder builder = new TopologyBuilder();
		
		// 设置topology中的spout
		builder.setSpout("kafka_spout_id", new KafkaSpout(spoutConf),6);
		// 设置bolt
		builder.setBolt("split_bolt_id", new SplitBolt(),3).shuffleGrouping("kafka_spout_id");
		builder.setBolt("static_bolt_id", new StaticBolt(),3).shuffleGrouping("split_bolt_id");
//		builder.setBolt("word-normalizer", new WordNormalizer()).shuffleGrouping("split_bolt_id");
//		builder.setBolt("word-counter", new WordCounter(), 1).fieldsGrouping("word-normalizer", new Fields("word"));

		// 启动topology
		StormTopology stormTopology = builder.createTopology();
		String topologyName = StatisticsPVAndUVTopology.class.getSimpleName();
		Config config = new Config();
		

		
		if (args == null || args.length < 1) {
			LocalCluster cluster = new LocalCluster();
//			config.set
			cluster.submitTopology(topologyName, config, stormTopology);
		} else {
			StormSubmitter.submitTopology(topologyName, config, stormTopology);
		}
	}

}
