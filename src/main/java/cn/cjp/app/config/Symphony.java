package cn.cjp.app.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;

@Data
@ConfigurationProperties(prefix = "symphony", ignoreUnknownFields = true)
public class Symphony {

	private boolean enable;

	private String staticServerPath;

	private String serverPath;

	private Login login;

	private Tuling tuling;

	@Data
	public static class Tuling {
		private String api;
		private String apikey;
		private String secret;
	}

	@Data
	public static class Login {

		private String secret;

	}

}
