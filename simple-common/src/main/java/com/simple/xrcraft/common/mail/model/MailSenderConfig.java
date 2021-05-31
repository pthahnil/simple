package com.simple.xrcraft.common.mail.model;

import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;

/**
 * @description:
 * @author pthahnil
 * @date 2021/5/31 9:11
 */
@Data
public class MailSenderConfig {

	@NotBlank(message = "邮件的host不能为空")
	private String host;

	@NotBlank(message = "用户名不能为空")
	private String username;

	@NotBlank(message = "密码名不能为空")
	private String password;

	public MailSenderConfig(String host, String username, String password) {
		this.host = host;
		this.username = username;
		this.password = password;
	}

	public MailSenderConfig() {
	}
}
