//package redis;
//
//import java.sql.Connection;
//import java.sql.SQLException;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//import java.util.Set;
//
//import clojure.main;
//import clojure.string__init;
//import jdbcutil.SqlService;
//import redis.clients.jedis.BinaryClient.LIST_POSITION;
//import redis.clients.jedis.Jedis;
//import redis.clients.jedis.JedisPool;
//import redis.clients.jedis.JedisPoolConfig;
//
///**
// * <p>
// * redis通用工具类
// * </p>
// * 
// * @author
// * @date 2016-02-24
// */
//public class JRedisUtil {
//	private JedisPool pool = null;
//
//	private static class RedisService {
//		private static JRedisUtil redis = new JRedisUtil("localhost", 6379);
//	}
//
//	public static JRedisUtil getJRedis() {
//		return RedisService.redis;
//	}
//
//	public static void main(String[] args) {
//
//		// Connection c = new SqlService().getConnection();
//
//		System.out.println(SqlService.execUpdate("INSERT INTO logtime(id,logtime) VALUES(?,?)", new Object[]{1,"2015-1-16"}));
//
//		JRedisUtil ju = JRedisUtil.getJRedis();
//		ju.set("wangzigui", "sdfdsf");
//		Map<String, String> hash = new HashMap<>();
//		hash.put("id", "1");
//		hash.put("time", "12345678");
//		ju.hmset("ww", hash);
//		System.out.println(ju.hget("ww", "id"));
//		System.out.println(ju.mget("runoobkey", "超人").toString());
//		System.out.println(ju.get("runoobkey"));
//		System.out.println(ju.exists("runoobkey"));
//	}
//
//	/**
//	 * <p>
//	 * 传入ip和端口号构建redis 连接池
//	 * </p>
//	 * 
//	 * @param ip
//	 *            ip
//	 * @param prot
//	 *            端口
//	 */
//	private JRedisUtil(String ip, int prot) {
//		if (pool == null) {
//			JedisPoolConfig config = new JedisPoolConfig();
//			// 控制一个pool可分配多少个jedis实例，通过pool.getResource()来获取；
//			// 如果赋值为-1，则表示不限制；如果pool已经分配了maxActive个jedis实例，则此时pool的状态为exhausted(耗尽)。
//			config.setMaxTotal(-1);
//			// 控制一个pool最多有多少个状态为idle(空闲的)的jedis实例。
//			config.setMaxIdle(8);
//			// 表示当borrow(引入)一个jedis实例时，最大的等待时间，如果超过等待时间，则直接抛出JedisConnectionException；
//			config.setMaxWaitMillis(100 * 100);
//			// 在borrow一个jedis实例时，是否提前进行validate操作；如果为true，则得到的jedis实例均是可用的；
//			config.setTestOnBorrow(true);
//			// pool = new JedisPool(config, "192.168.0.121", 6379, 100000);
//			pool = new JedisPool(config, ip, prot, 100000);
//		}
//	}
//
//	/**
//	 * <p>
//	 * 通过配置对象 ip 端口 构建连接池
//	 * </p>
//	 * 
//	 * @param config
//	 *            配置对象
//	 * @param ip
//	 *            ip
//	 * @param prot
//	 *            端口
//	 */
//	private JRedisUtil(JedisPoolConfig config, String ip, int prot) {
//		if (pool == null) {
//			pool = new JedisPool(config, ip, prot, 10000);
//		}
//	}
//
//	/**
//	 * <p>
//	 * 通过配置对象 ip 端口 超时时间 构建连接池
//	 * </p>
//	 * 
//	 * @param config
//	 *            配置对象
//	 * @param ip
//	 *            ip
//	 * @param prot
//	 *            端口
//	 * @param timeout
//	 *            超时时间
//	 */
//	private JRedisUtil(JedisPoolConfig config, String ip, int prot, int timeout) {
//		if (pool == null) {
//			pool = new JedisPool(config, ip, prot, timeout);
//		}
//	}
//
//	/**
//	 * <p>
//	 * 通过连接池对象 构建一个连接池
//	 * </p>
//	 * 
//	 * @param pool
//	 *            连接池对象
//	 */
//	private JRedisUtil(JedisPool pool) {
//		if (this.pool == null) {
//			this.pool = pool;
//		}
//	}
//
//	private JRedisUtil() {
//	}
//
//	/**
//	 * <p>
//	 * 通过key获取储存在redis中的value
//	 * </p>
//	 * <p>
//	 * 并释放连接
//	 * </p>
//	 * 
//	 * @param key
//	 * @return 成功返回value 失败返回null
//	 */
//	public String get(String key) {
//		Jedis jedis = null;
//		String value = null;
//		try {
//			jedis = pool.getResource();
//			value = jedis.get(key);
//		} catch (Exception e) {
//			pool.returnBrokenResource(jedis);
//			e.printStackTrace();
//		} finally {
//			// // returnResource(pool, jedis);
//		}
//		return value;
//	}
//
//	/**
//	 * <p>
//	 * 向redis存入key和value,并释放连接资源
//	 * </p>
//	 * <p>
//	 * 如果key已经存在 则覆盖
//	 * </p>
//	 * 
//	 * @param key
//	 * @param value
//	 * @return 成功 返回OK 失败返回 0
//	 */
//	public String set(String key, String value) {
//		Jedis jedis = null;
//		try {
//			jedis = pool.getResource();
//			return jedis.set(key, value);
//		} catch (Exception e) {
//			pool.returnBrokenResource(jedis);
//			e.printStackTrace();
//			return "0";
//		} finally {
//			// // returnResource(pool, jedis);
//		}
//	}
//
//	/**
//	 * <p>
//	 * 删除指定的key,也可以传入一个包含key的数组
//	 * </p>
//	 * 
//	 * @param keys
//	 *            一个key 也可以使 string 数组
//	 * @return 返回删除成功的个数
//	 */
//	public Long del(String... keys) {
//		Jedis jedis = null;
//		try {
//			jedis = pool.getResource();
//			return jedis.del(keys);
//		} catch (Exception e) {
//			pool.returnBrokenResource(jedis);
//			e.printStackTrace();
//			return 0L;
//		} finally {
//			// // returnResource(pool, jedis);
//		}
//	}
//
//	/**
//	 * <p>
//	 * 通过key向指定的value值追加值
//	 * </p>
//	 * 
//	 * @param key
//	 * @param str
//	 * @return 成功返回 添加后value的长度 失败 返回 添加的 value 的长度 异常返回0L
//	 */
//	public Long append(String key, String str) {
//		Jedis jedis = null;
//		Long res = null;
//		try {
//			jedis = pool.getResource();
//			res = jedis.append(key, str);
//		} catch (Exception e) {
//			pool.returnBrokenResource(jedis);
//			e.printStackTrace();
//			return 0L;
//		} finally {
//			// // returnResource(pool, jedis);
//		}
//		return res;
//	}
//
//	/**
//	 * <p>
//	 * 判断key是否存在
//	 * </p>
//	 * 
//	 * @param key
//	 * @return true OR false
//	 */
//	public Boolean exists(String key) {
//		Jedis jedis = null;
//		try {
//			jedis = pool.getResource();
//			return jedis.exists(key);
//		} catch (Exception e) {
//			pool.returnBrokenResource(jedis);
//			e.printStackTrace();
//			return false;
//		} finally {
//			// // returnResource(pool, jedis);
//		}
//	}
//
//	/**
//	 * <p>
//	 * 设置key value,如果key已经存在则返回0,nx==> not exist
//	 * </p>
//	 * 
//	 * @param key
//	 * @param value
//	 * @return 成功返回1 如果存在 和 发生异常 返回 0
//	 */
//	public Long setnx(String key, String value) {
//		Jedis jedis = null;
//		try {
//			jedis = pool.getResource();
//			return jedis.setnx(key, value);
//		} catch (Exception e) {
//			pool.returnBrokenResource(jedis);
//			e.printStackTrace();
//			return 0L;
//		} finally {
//			// // returnResource(pool, jedis);
//		}
//	}
//
//	/**
//	 * <p>
//	 * 设置key value并制定这个键值的有效期
//	 * </p>
//	 * 
//	 * @param key
//	 * @param value
//	 * @param seconds
//	 *            单位:秒
//	 * @return 成功返回OK 失败和异常返回null
//	 */
//	public String setex(String key, String value, int seconds) {
//		Jedis jedis = null;
//		String res = null;
//		try {
//			jedis = pool.getResource();
//			res = jedis.setex(key, seconds, value);
//		} catch (Exception e) {
//			pool.returnBrokenResource(jedis);
//			e.printStackTrace();
//		} finally {
//			// // returnResource(pool, jedis);
//		}
//		return res;
//	}
//
//	/**
//	 * <p>
//	 * 通过key 和offset 从指定的位置开始将原先value替换
//	 * </p>
//	 * <p>
//	 * 下标从0开始,offset表示从offset下标开始替换
//	 * </p>
//	 * <p>
//	 * 如果替换的字符串长度过小则会这样
//	 * </p>
//	 * <p>
//	 * example:
//	 * </p>
//	 * <p>
//	 * value : bigsea@zto.cn
//	 * </p>
//	 * <p>
//	 * str : abc
//	 * </p>
//	 * <P>
//	 * 从下标7开始替换 则结果为
//	 * </p>
//	 * <p>
//	 * RES : bigsea.abc.cn
//	 * </p>
//	 * 
//	 * @param key
//	 * @param str
//	 * @param offset
//	 *            下标位置
//	 * @return 返回替换后 value 的长度
//	 */
//	public Long setrange(String key, String str, int offset) {
//		Jedis jedis = null;
//		try {
//			jedis = pool.getResource();
//			return jedis.setrange(key, offset, str);
//		} catch (Exception e) {
//			pool.returnBrokenResource(jedis);
//			e.printStackTrace();
//			return 0L;
//		} finally {
//			// returnResource(pool, jedis);
//		}
//	}
//
//	/**
//	 * <p>
//	 * 通过批量的key获取批量的value
//	 * </p>
//	 * 
//	 * @param keys
//	 *            string数组 也可以是一个key
//	 * @return 成功返回value的集合, 失败返回null的集合 ,异常返回空
//	 */
//	public List<String> mget(String... keys) {
//		Jedis jedis = null;
//		List<String> values = null;
//		try {
//			jedis = pool.getResource();
//			values = jedis.mget(keys);
//		} catch (Exception e) {
//			pool.returnBrokenResource(jedis);
//			e.printStackTrace();
//		} finally {
//			// returnResource(pool, jedis);
//		}
//		return values;
//	}
//
//	/**
//	 * <p>
//	 * 批量的设置key:value,可以一个
//	 * </p>
//	 * <p>
//	 * example:
//	 * </p>
//	 * <p>
//	 * obj.mset(new String[]{"key2","value1","key2","value2"})
//	 * </p>
//	 * 
//	 * @param keysvalues
//	 * @return 成功返回OK 失败 异常 返回 null
//	 * 
//	 */
//	public String mset(String... keysvalues) {
//		Jedis jedis = null;
//		String res = null;
//		try {
//			jedis = pool.getResource();
//			res = jedis.mset(keysvalues);
//		} catch (Exception e) {
//			pool.returnBrokenResource(jedis);
//			e.printStackTrace();
//		} finally {
//			// returnResource(pool, jedis);
//		}
//		return res;
//	}
//
//	/**
//	 * <p>
//	 * 批量的设置key:value,可以一个,如果key已经存在则会失败,操作会回滚
//	 * </p>
//	 * <p>
//	 * example:
//	 * </p>
//	 * <p>
//	 * obj.msetnx(new String[]{"key2","value1","key2","value2"})
//	 * </p>
//	 * 
//	 * @param keysvalues
//	 * @return 成功返回1 失败返回0
//	 */
//	public Long msetnx(String... keysvalues) {
//		Jedis jedis = null;
//		Long res = 0L;
//		try {
//			jedis = pool.getResource();
//			res = jedis.msetnx(keysvalues);
//		} catch (Exception e) {
//			pool.returnBrokenResource(jedis);
//			e.printStackTrace();
//		} finally {
//			// returnResource(pool, jedis);
//		}
//		return res;
//	}
//
//	/**
//	 * <p>
//	 * 设置key的值,并返回一个旧值
//	 * </p>
//	 * 
//	 * @param key
//	 * @param value
//	 * @return 旧值 如果key不存在 则返回null
//	 */
//	public String getset(String key, String value) {
//		Jedis jedis = null;
//		String res = null;
//		try {
//			jedis = pool.getResource();
//			res = jedis.getSet(key, value);
//		} catch (Exception e) {
//			pool.returnBrokenResource(jedis);
//			e.printStackTrace();
//		} finally {
//			// returnResource(pool, jedis);
//		}
//		return res;
//	}
//
//	/**
//	 * <p>
//	 * 通过下标 和key 获取指定下标位置的 value
//	 * </p>
//	 * 
//	 * @param key
//	 * @param startOffset
//	 *            开始位置 从0 开始 负数表示从右边开始截取
//	 * @param endOffset
//	 * @return 如果没有返回null
//	 */
//	public String getrange(String key, int startOffset, int endOffset) {
//		Jedis jedis = null;
//		String res = null;
//		try {
//			jedis = pool.getResource();
//			res = jedis.getrange(key, startOffset, endOffset);
//		} catch (Exception e) {
//			pool.returnBrokenResource(jedis);
//			e.printStackTrace();
//		} finally {
//			// returnResource(pool, jedis);
//		}
//		return res;
//	}
//
//	/**
//	 * <p>
//	 * 通过key 对value进行加值+1操作,当value不是int类型时会返回错误,当key不存在是则value为1
//	 * </p>
//	 * 
//	 * @param key
//	 * @return 加值后的结果
//	 */
//	public Long incr(String key) {
//		Jedis jedis = null;
//		Long res = null;
//		try {
//			jedis = pool.getResource();
//			res = jedis.incr(key);
//		} catch (Exception e) {
//			pool.returnBrokenResource(jedis);
//			e.printStackTrace();
//		} finally {
//			// returnResource(pool, jedis);
//		}
//		return res;
//	}
//
//	/**
//	 * <p>
//	 * 通过key给指定的value加值,如果key不存在,则这是value为该值
//	 * </p>
//	 * 
//	 * @param key
//	 * @param integer
//	 * @return
//	 */
//	public Long incrBy(String key, Long integer) {
//		Jedis jedis = null;
//		Long res = null;
//		try {
//			jedis = pool.getResource();
//			res = jedis.incrBy(key, integer);
//		} catch (Exception e) {
//			pool.returnBrokenResource(jedis);
//			e.printStackTrace();
//		} finally {
//			// returnResource(pool, jedis);
//		}
//		return res;
//	}
//
//	/**
//	 * <p>
//	 * 对key的值做减减操作,如果key不存在,则设置key为-1
//	 * </p>
//	 * 
//	 * @param key
//	 * @return
//	 */
//	public Long decr(String key) {
//		Jedis jedis = null;
//		Long res = null;
//		try {
//			jedis = pool.getResource();
//			res = jedis.decr(key);
//		} catch (Exception e) {
//			pool.returnBrokenResource(jedis);
//			e.printStackTrace();
//		} finally {
//			// returnResource(pool, jedis);
//		}
//		return res;
//	}
//
//	/**
//	 * <p>
//	 * 减去指定的值
//	 * </p>
//	 * 
//	 * @param key
//	 * @param integer
//	 * @return
//	 */
//	public Long decrBy(String key, Long integer) {
//		Jedis jedis = null;
//		Long res = null;
//		try {
//			jedis = pool.getResource();
//			res = jedis.decrBy(key, integer);
//		} catch (Exception e) {
//			pool.returnBrokenResource(jedis);
//			e.printStackTrace();
//		} finally {
//			// returnResource(pool, jedis);
//		}
//		return res;
//	}
//
//	/**
//	 * <p>
//	 * 通过key获取value值的长度
//	 * </p>
//	 * 
//	 * @param key
//	 * @return 失败返回null
//	 */
//	public Long serlen(String key) {
//		Jedis jedis = null;
//		Long res = null;
//		try {
//			jedis = pool.getResource();
//			res = jedis.strlen(key);
//		} catch (Exception e) {
//			pool.returnBrokenResource(jedis);
//			e.printStackTrace();
//		} finally {
//			// returnResource(pool, jedis);
//		}
//		return res;
//	}
//
//	/**
//	 * <p>
//	 * 通过key给field设置指定的值,如果key不存在,则先创建
//	 * </p>
//	 * 
//	 * @param key
//	 * @param field
//	 *            字段
//	 * @param value
//	 * @return 如果存在返回0 异常返回null
//	 */
//	public Long hset(String key, String field, String value) {
//		Jedis jedis = null;
//		Long res = null;
//		try {
//			jedis = pool.getResource();
//			res = jedis.hset(key, field, value);
//		} catch (Exception e) {
//			pool.returnBrokenResource(jedis);
//			e.printStackTrace();
//		} finally {
//			// returnResource(pool, jedis);
//		}
//		return res;
//	}
//
//	/**
//	 * <p>
//	 * 通过key给field设置指定的值,如果key不存在则先创建,如果field已经存在,返回0
//	 * </p>
//	 * 
//	 * @param key
//	 * @param field
//	 * @param value
//	 * @return
//	 */
//	public Long hsetnx(String key, String field, String value) {
//		Jedis jedis = null;
//		Long res = null;
//		try {
//			jedis = pool.getResource();
//			res = jedis.hsetnx(key, field, value);
//		} catch (Exception e) {
//			pool.returnBrokenResource(jedis);
//			e.printStackTrace();
//		} finally {
//			// returnResource(pool, jedis);
//		}
//		return res;
//	}
//
//	/**
//	 * <p>
//	 * 通过key同时设置 hash的多个field
//	 * </p>
//	 * 
//	 * @param key
//	 * @param hash
//	 * @return 返回OK 异常返回null
//	 */
//	public String hmset(String key, Map<String, String> hash) {
//		Jedis jedis = null;
//		String res = null;
//		try {
//			jedis = pool.getResource();
//			res = jedis.hmset(key, hash);
//		} catch (Exception e) {
//			pool.returnBrokenResource(jedis);
//			e.printStackTrace();
//		} finally {
//			// returnResource(pool, jedis);
//		}
//		return res;
//	}
//
//	/**
//	 * <p>
//	 * 通过key 和 field 获取指定的 value
//	 * </p>
//	 * 
//	 * @param key
//	 * @param field
//	 * @return 没有返回null
//	 */
//	public String hget(String key, String field) {
//		Jedis jedis = null;
//		String res = null;
//		try {
//			jedis = pool.getResource();
//			res = jedis.hget(key, field);
//		} catch (Exception e) {
//			pool.returnBrokenResource(jedis);
//			e.printStackTrace();
//		} finally {
//			// returnResource(pool, jedis);
//		}
//		return res;
//	}
//
//	/**
//	 * <p>
//	 * 通过key 和 fields 获取指定的value 如果没有对应的value则返回null
//	 * </p>
//	 * 
//	 * @param key
//	 * @param fields
//	 *            可以使 一个String 也可以是 String数组
//	 * @return
//	 */
//	public List<String> hmget(String key, String... fields) {
//		Jedis jedis = null;
//		List<String> res = null;
//		try {
//			jedis = pool.getResource();
//			res = jedis.hmget(key, fields);
//		} catch (Exception e) {
//			pool.returnBrokenResource(jedis);
//			e.printStackTrace();
//		} finally {
//			// returnResource(pool, jedis);
//		}
//		return res;
//	}
//
//	/**
//	 * <p>
//	 * 通过key给指定的field的value加上给定的值
//	 * </p>
//	 * 
//	 * @param key
//	 * @param field
//	 * @param value
//	 * @return
//	 */
//	public Long hincrby(String key, String field, Long value) {
//		Jedis jedis = null;
//		Long res = null;
//		try {
//			jedis = pool.getResource();
//			res = jedis.hincrBy(key, field, value);
//		} catch (Exception e) {
//			pool.returnBrokenResource(jedis);
//			e.printStackTrace();
//		} finally {
//			// returnResource(pool, jedis);
//		}
//		return res;
//	}
//
//	/**
//	 * <p>
//	 * 通过key和field判断是否有指定的value存在
//	 * </p>
//	 * 
//	 * @param key
//	 * @param field
//	 * @return
//	 */
//	public Boolean hexists(String key, String field) {
//		Jedis jedis = null;
//		Boolean res = false;
//		try {
//			jedis = pool.getResource();
//			res = jedis.hexists(key, field);
//		} catch (Exception e) {
//			pool.returnBrokenResource(jedis);
//			e.printStackTrace();
//		} finally {
//			// returnResource(pool, jedis);
//		}
//		return res;
//	}
//
//	/**
//	 * <p>
//	 * 通过key返回field的数量
//	 * </p>
//	 * 
//	 * @param key
//	 * @return
//	 */
//	public Long hlen(String key) {
//		Jedis jedis = null;
//		Long res = null;
//		try {
//			jedis = pool.getResource();
//			res = jedis.hlen(key);
//		} catch (Exception e) {
//			pool.returnBrokenResource(jedis);
//			e.printStackTrace();
//		} finally {
//			// returnResource(pool, jedis);
//		}
//		return res;
//	}
//
//	/**
//	 * <p>
//	 * 通过key 删除指定的 field
//	 * </p>
//	 * 
//	 * @param key
//	 * @param fields
//	 *            可以是 一个 field 也可以是 一个数组
//	 * @return
//	 */
//	public Long hdel(String key, String... fields) {
//		Jedis jedis = null;
//		Long res = null;
//		try {
//			jedis = pool.getResource();
//			res = jedis.hdel(key, fields);
//		} catch (Exception e) {
//			pool.returnBrokenResource(jedis);
//			e.printStackTrace();
//		} finally {
//			// returnResource(pool, jedis);
//		}
//		return res;
//	}
//
//	/**
//	 * <p>
//	 * 通过key返回所有的field
//	 * </p>
//	 * 
//	 * @param key
//	 * @return
//	 */
//	public Set<String> hkeys(String key) {
//		Jedis jedis = null;
//		Set<String> res = null;
//		try {
//			jedis = pool.getResource();
//			res = jedis.hkeys(key);
//		} catch (Exception e) {
//			pool.returnBrokenResource(jedis);
//			e.printStackTrace();
//		} finally {
//			// returnResource(pool, jedis);
//		}
//		return res;
//	}
//
//	/**
//	 * <p>
//	 * 通过key返回所有和key有关的value
//	 * </p>
//	 * 
//	 * @param key
//	 * @return
//	 */
//	public List<String> hvals(String key) {
//		Jedis jedis = null;
//		List<String> res = null;
//		try {
//			jedis = pool.getResource();
//			res = jedis.hvals(key);
//		} catch (Exception e) {
//			pool.returnBrokenResource(jedis);
//			e.printStackTrace();
//		}
//		return res;
//	}
//}