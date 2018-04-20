package bolts;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.BasicOutputCollector;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseBasicBolt;
import backtype.storm.topology.base.BaseRichBolt;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Tuple;
import backtype.storm.tuple.Values;
//import redis.JRedisUtil;

//获取spout从kafka中提取的数据，进行操作
public class SplitBolt extends BaseBasicBolt{
	private Map conf;
	private TopologyContext context;
//	private OutputCollector collector;

	public void prepare(Map conf, TopologyContext context) {
		this.conf = conf;
		this.context = context;
//		this.collector = collector;
	}

	/**
	 * java.lang.ClassCastException: [B cannot be cast to java.lang.String
	 * 数据在kafka中直接存储的是字节数组，不能使用字符串进行接收
	 * 
	 * @param tuple
	 */
	public void execute(Tuple tuple, BasicOutputCollector collector) {
		String line = new String(tuple.getBinary(0));

		// String[] items = line.split("##");
		// if(items == null || items.length < 2) {
		// return;
		// }
		// System.out.println("kafka中的数据：ip--->" + items[0] + ", time--->" +
		// items[1]);
//		JRedisUtil ju = JRedisUtil.getJRedis();
//    	Map<String, String> hash = new HashMap<>();
//    	hash.put("id", line);
//    	hash.put("time", new Date().toString());
//    	ju.hmset(line, hash);
//    	System.out.println(ju.hget(line, "time"));
		System.out.println("kafka中的数据：ip--->" + line);
//		this.collector.
		collector.emit(new Values(line));
		

//		collector.ack(tuple);
	}

	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		declarer.declare(new Fields("ip"));
	}

	
}
