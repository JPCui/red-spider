package cn.cjp.spider;

import java.util.Set;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import cn.cjp.utils.StringUtil;
import redis.clients.jedis.Jedis;

public class RedisTest {

	Jedis jedis = null;

	@Before
	public void before() {
		jedis = new Jedis();
	}

	@After
	public void after() {
		jedis.close();
	}

	/**
	 * 从list移动到set，用于去重
	 */
	@Test
	public void list2Set() {
		String listName = "queue_99lib.net";
		String setName = "tmp_set_queue_99lib.net";
		this.listToSet(listName, setName);
	}

	@Test
	public void set2List() {
		String listName = "queue_99lib.net";
		String setName = "tmp_set_queue_99lib.net";
		this.setToList(setName, listName);
	}

	private void listToSet(String listName, String setName) {
		int num = 0;
		Long listLength = jedis.llen(listName);
		while (true) {
			String v = jedis.rpop(listName);
			if (StringUtil.isEmpty(v)) {
				break;
			}
			jedis.sadd(setName, v);
			if ((num++) % 1000 == 0) {
				System.out.println("moved: " + num);
			}
			if (num >= 10_000_000) {
				break;
			}
		}

		Long setLength = jedis.scard(setName);

		System.out.println("list length: " + listLength);
		System.out.println("set length: " + setLength);
	}

	private void setToList(String setName, String listName) {
		int num = 0;
		Long setLength = jedis.scard(setName);
		while (true) {
			String v = jedis.spop(setName);
			if (StringUtil.isEmpty(v)) {
				break;
			}
			jedis.lpush(listName, v);
			if ((num++) % 1000 == 0) {
				System.out.println("moved: " + num);
			}
		}

		Long listLength = jedis.llen(listName);

		System.out.println("list length: " + listLength);
		System.out.println("set length: " + setLength);
	}

	@Test
	public void diffSet() {
		String set1 = "set_99lib.net";
		String set2 = "set_autual_99lib.net";
		String queue = "queue_99lib.net";

		// 取出差集
		Set<String> diffSet = jedis.sdiff(set1, set2);
		System.out.println("diff : " + diffSet);
		// 从全集合去除这些集合
		String[] diffs = diffSet.toArray(new String[0]);
		jedis.srem(set1, diffs);
		// 然后放入队列
		jedis.lpush(queue, diffs);

	}

}
