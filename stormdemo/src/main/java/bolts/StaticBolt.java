package bolts;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import backtype.storm.Config;
import backtype.storm.Constants;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.BasicOutputCollector;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseBasicBolt;
import backtype.storm.tuple.Tuple;
import dbutil.C3p0Utils;

/**
 * 分时段统计PV和UV 接收到的数据： ip--->27.19.74.143 time->2016-05-30 17:38:20
 *
 * 我们提取出时段，每一个时段都对应了一个ip地址的集合 我们可以将时段作为key，ip地址对应的集合作为一个value Map<String,
 * List<String>>;
 *
 * key--->list的大小(PV),---->UV list====>set--->set.size-->UV
 *
 *
 *
 */
public class StaticBolt extends BaseBasicBolt {
	private Map conf;
	private TopologyContext context;
//	private OutputCollector collector;

	public void prepare(Map conf, TopologyContext context) {
		this.conf = conf;
		this.context = context;
//		this.collector = collector;
	}

	Map<String, List<String>> map = new HashMap<String, List<String>>();
	List<String> list = null;
	Set<String> set = null;

	@Override
	public void execute(Tuple input, BasicOutputCollector collector) {
		// String hour = null;
//		input.getStringByField("");
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		System.out.println(sdf.format(new Date()));
//		if (!input.getSourceComponent().equalsIgnoreCase(Constants.SYSTEM_COMPONENT_ID)) {
			String line = new String(input.getStringByField("ip"));
			System.out.println("kafka中的数据：ip--->aaa" + line);
			
			
			try {
				C3p0Utils.execUpdate("insert into T_IDC_BANK_DESC(ID,NAME,DESCRibe) values(?,?,?)", new Object[]{new Random().nextInt(100000),line,sdf.format(new Date())});
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
//		}
//		collector.ack(input);
		// if(!input.getSourceComponent().equalsIgnoreCase(Constants.SYSTEM_COMPONENT_ID))
		// {
		// String ip = input.getStringByField("ip");
		// String timeStr = input.getStringByField("time");
		// hour = timeStr.substring(11, 13);
		//
		// list = map.get(hour);//key一定是时间段hour，不是ip
		// if (list == null) {
		// System.out.println("-----1111-----");
		// list = new ArrayList<>();
		// }
		// list.add(ip);
		// map.put(hour, list);//每一个时段所对应的ip地址的集合
		// }
		// else {
		// //求出PU和UV
		// System.out.println("===============start================");
		// System.out.println(map);
		// System.out.println("------------------------------------");
		// Jedis jedis = JedisUtils.getJedis();
		// for (Map.Entry<String, List<String>> me : map.entrySet()) {
		// hour = me.getKey();
		// list = me.getValue();
		// int pv = list.size();
		// set = new HashSet<String>(list);
		// int uv = set.size();
		//// System.out.println("hour: " + hour + ",pv: " + pv + ",uv:" + uv);
		// //将数据写到redis
		// jedis.hset("pv_uv_" + hour, "pv", pv + "");
		// jedis.hset("pv_uv_" + hour, "uv", uv + "");
		// }
		// JedisUtils.close(jedis);
		// System.out.println("===============end=================");
		// }
	}

	@Override
	public void declareOutputFields(OutputFieldsDeclarer declarer) {

	}

//	@Override
//	public Map<String, Object> getComponentConfiguration() {
//		Map<String, Object> conf = new HashMap<>();
//		conf.put(Config.TOPOLOGY_TICK_TUPLE_FREQ_SECS, 10);
//		return conf;
//	}

}
