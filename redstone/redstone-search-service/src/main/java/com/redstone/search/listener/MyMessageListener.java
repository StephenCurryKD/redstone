package com.redstone.search.listener;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

/**
 * 接收消息ActiveMQ发送的消息
 * <p>Title:MyMessageListener</p>
 * <p>Description:</p>
 * @author sky
 * @version V1.0
 */
public class MyMessageListener implements MessageListener {

	@Override
	public void onMessage(Message message) {
		//接收到消息
		try {
			TextMessage textMessage=(TextMessage) message;
			String text= textMessage.getText();
			System.out.println(text);
		} catch (JMSException e) {
			e.printStackTrace();
		}
	}

}
