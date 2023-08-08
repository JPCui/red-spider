package cn.cjp.spider.core.scheduler;

import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.scheduler.RedisScheduler;

/**
 * 衹有在URL爬取成功后，URL才會sadd進set集合裏
 */
@Slf4j
public class MyRedisScheduler extends RedisScheduler {

	/**
	 * 真实抓取到的URL集合（抓取成功才会入队）
	 */
	private static final String SET_AUTUAL_PREFIX = "set_autual_";

	protected String getActualSetKey(Task task) {
		return SET_AUTUAL_PREFIX + task.getUUID();
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
			// Long r = jedis.sadd(getSetKey(task), request.getUrl());
			Long r = jedis.sadd(getActualSetKey(task), request.getUrl());
			if (Long.valueOf(1L).equals(r)) {
				log.info(String.format("download url success : %s", request.getUrl()));
			} else {
				log.warn(String.format("download url duplicate : %s", request.getUrl()));
			}
		}
	}

	// @Override
	// public boolean isDuplicate(Request request, Task task) {
	// try (Jedis jedis = pool.getResource()) {
	// ScanResult<String> scanResult = jedis.sscan(getSetKey(task), "0", new
	// ScanParams().match(request.getUrl()));
	// return scanResult.getResult().size() != 0;
	// }
	// }

}
