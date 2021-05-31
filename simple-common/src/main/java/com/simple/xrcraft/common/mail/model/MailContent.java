package com.simple.xrcraft.common.mail.model;

import lombok.Data;
import org.springframework.core.io.Resource;

import javax.validation.constraints.NotBlank;

/**
 * @description:
 * @author pthahnil
 * @date 2021/5/31 9:33
 */
@Data
public class MailContent {

	@NotBlank(message = "邮件接收人不能为空")
	private String[] receivers;

	@NotBlank(message = "邮件主题为空")
	private String subject;

	@NotBlank(message = "邮件发送人为空")
	private String sender;

	//回复
	private String replyTo;

	//抄送
	private String cc;

	//文本内容
	private String text;

	//是否富文本
	private boolean isHtml = false;

	//附件
	private Resource[] attachments;

}
