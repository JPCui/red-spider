package cn.cjp.spider.core.scheduler;

import cn.cjp.utils.Logger;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.ScanParams;
import redis.clients.jedis.ScanResult;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.scheduler.RedisScheduler;

/**
 * 衹有在URL爬取成功后，URL才會sadd進set集合裏
 */
public class MyRedisScheduler extends RedisScheduler {

	private static final Logger LOGGER = Logger.getLogger(MyRedisScheduler.class);

	public MyRedisScheduler(String host) {
		super(host);
	}

	public MyRedisScheduler(JedisPool pool) {
		super(pool);
	}

	/**
	 * sadd
	 * 
	 * @param request
	 * @param task
	 */
	public void onDownloadSuccess(Request request, Task task) {
		try (Jedis jedis = pool.getResource()) {
			Long r = jedis.sadd(getSetKey(task), request.getUrl());
			if (Long.valueOf(1L) == r) {
				LOGGER.info(String.format("download url success : %s", request.getUrl()));
			} else {
				LOGGER.warn(String.format("download url duplicate : %s", request.getUrl()));
			}
		}
	}

	@Override
	public boolean isDuplicate(Request request, Task task) {
		try (Jedis jedis = pool.getResource()) {
			ScanResult<String> scanResult = jedis.sscan(getSetKey(task), "0", new ScanParams().match(request.getUrl()));
			return scanResult.getResult().size() != 0;
		}
	}

}
