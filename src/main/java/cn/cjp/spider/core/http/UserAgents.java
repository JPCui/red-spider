package cn.cjp.spider.core.http;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang.math.RandomUtils;
import org.springframework.util.ResourceUtils;

import cn.cjp.utils.FileUtil;
import cn.cjp.utils.Logger;

public class UserAgents {

	private static final Logger LOGGER = Logger.getLogger(UserAgents.class);

	private static final List<String> userAgents = new ArrayList<>(200);

	static {
		try {
			File userAgentConf = ResourceUtils.getFile("classpath:user-agent.conf");
			List<String> confs = FileUtil.read(userAgentConf);
			userAgents.addAll(confs.stream().filter(s -> !s.startsWith("#") || !s.startsWith("//")).collect(Collectors.toList()));
		} catch (FileNotFoundException e) {
			LOGGER.error(String.format("user-agent.conf read fail"), e);
		}
	}

	/**
	 * get a random user-agent
	 * 
	 * @return
	 */
	public static String get() {
		int i = userAgents.size();
		return userAgents.get(RandomUtils.nextInt(i));
	}

	public static void main(String[] args) {
		System.out.println(get());
	}

}
