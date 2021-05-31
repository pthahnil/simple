package com.simple.xrcraft.common.mail;

import com.simple.xrcraft.common.mail.model.MailContent;
import com.simple.xrcraft.common.mail.model.MailSenderConfig;
import com.simple.xrcraft.common.utils.validate.BeanValidator;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.io.Resource;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

/**
 * @description:
 * @author pthahnil
 * @date 2021/5/31 9:09
 */
public class JavaMailUtil {

	/**
	 * 邮件内容组装
	 * @param content
	 * @return
	 * @throws Exception
	 */
	public static MimeMessage getMimeMessage(JavaMailSenderImpl mailSender, MailContent content) throws Exception {

		String msg = BeanValidator.validate(content);
		if(StringUtils.isNotBlank(msg)){
			throw new RuntimeException(msg);
		}

		MimeMessage mimeMessage = mailSender.createMimeMessage();
		MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
		messageHelper.setTo(content.getReceivers());
		messageHelper.setFrom(content.getSender());
		messageHelper.setSubject(content.getSubject());
		if(StringUtils.isNotBlank(content.getText())){
			messageHelper.setText(content.getText(), content.isHtml());
		}
		if(StringUtils.isNotBlank(content.getReplyTo())){
			messageHelper.setReplyTo(content.getReplyTo());
		}
		if(StringUtils.isNotBlank(content.getCc())){
			messageHelper.setReplyTo(content.getCc());
		}
		if(null != content.getAttachments()){
			for (Resource attachment : content.getAttachments()) {
				if(!attachment.exists()){
					continue;
				}
				messageHelper.addAttachment(attachment.getFilename(), attachment);
			}
		}

		return mimeMessage;
	}


	/**
	 * 邮件发送配置
	 * @param config
	 * @return
	 */
	public static JavaMailSenderImpl getMailSender(MailSenderConfig config){
		return getMailSender(config, null);
	}

	/**
	 * 邮件发送配置
	 * @param config
	 * @param mailProps
	 * @return
	 */
	public static JavaMailSenderImpl getMailSender(MailSenderConfig config, Properties mailProps){

		String msg = BeanValidator.validate(config);
		if(StringUtils.isNotBlank(msg)){
			throw new RuntimeException(msg);
		}
		if(MapUtils.isEmpty(mailProps)){
			mailProps = new Properties();
			mailProps.setProperty("mail.smtp.auth", "true");
			mailProps.setProperty("mail.smtp.timeout", "10000");
		}

		JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
		mailSender.setHost(config.getHost());
		mailSender.setUsername(config.getUsername());
		mailSender.setPassword(config.getPassword());
		mailSender.setJavaMailProperties(mailProps);

		return mailSender;
	}

}
