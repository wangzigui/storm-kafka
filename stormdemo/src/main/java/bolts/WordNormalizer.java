package bolts;

import java.text.SimpleDateFormat;
import java.util.Date;

import backtype.storm.topology.BasicOutputCollector;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseBasicBolt;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Tuple;
import backtype.storm.tuple.Values;

public class WordNormalizer extends BaseBasicBolt {

	public void cleanup() {}

	/**
	 * The bolt will receive the line from the
	 * words file and process it to Normalize this line
	 * 
	 * The normalize will be put the words in lower case
	 * and split the line to get all words in this 
	 */
	public void execute(Tuple input, BasicOutputCollector collector) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		System.out.println(sdf.format(new Date()));
		String line = new String(input.getStringByField("ip"));
		System.out.println("kafka中的数据：ip--->aaa" + line);
//		System.out.println("kafka中的数据：ip--->" + sentence1);
//        byte[] sentence = input.getBinary(0);
//        String sentence1 = new String(sentence);
//        String[] words = sentence1.split(" ");
//        for(String word : words){
//            word = word.trim();
//            if(!word.isEmpty()){
//                word = word.toLowerCase();
//                collector.emit(new Values(word));
//            }
//        }
	}
	

	/**
	 * The bolt will only emit the field "word" 
	 */
	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		declarer.declare(new Fields("word"));
	}
}
