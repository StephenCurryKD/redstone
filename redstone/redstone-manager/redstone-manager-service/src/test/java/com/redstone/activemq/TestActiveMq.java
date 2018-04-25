package com.redstone.activemq;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.jms.Topic;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.junit.Test;

public class TestActiveMq {

	//queue：持久化到服务端
	//producer
	@Test
	public void testQueueProducer()throws Exception{
		//1.创建一个连接工厂ConnectionFactory对象。需要指定mq服务的ip和端口号
		ConnectionFactory connectionFactory=new ActiveMQConnectionFactory("tcp://47.100.114.233:61616");
		//2.使用ConnectionFactory创建一个连接Connection对象
		Connection connection = connectionFactory.createConnection();
		//3.开启连接。调用Connection的start方法
		connection.start();
		//4.使用Connection对象创建一个Session对象
		//第一个参数是是否开启事务。一般不使用事务(分布式事务,不同数据库之间处理事务)，保证数据的最终一致，可以使用消息队列实现
		//如果第一个参数为true，那么第二个参数自动忽略
		//第二个参数是消息的应答模式，1、自动应答 2、手动应答
		Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
		//5.使用Session对象创建一个Destination对象,两种形式queue、topic。现在应该使用queue。目的地
		//参数是消息队列的名称
		Queue queue = session.createQueue("test-q");
		//6.使用Session对象创建一个Producer对象,用于发送消息
		MessageProducer producer = session.createProducer(queue);
		//7.创建一个TextMessage对象。消息
		/*TextMessage textMessage=new ActiveMQTextMessage();
		textMessage.setText("hello,world!");*/
		TextMessage textMessage = session.createTextMessage("hello,world2!");
		//8.发送消息
		producer.send(textMessage);
		//9.关闭资源
		producer.close();
		session.close();
		connection.close();
	}
	
	//topic:不持久化到服务端
	//producer
	@Test
	public void testTopicProducer()throws Exception{
		ConnectionFactory connectionFactory=new ActiveMQConnectionFactory("tcp://47.100.114.233:61616");
		Connection connection = connectionFactory.createConnection();
		connection.start();
		Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
		Topic topic = session.createTopic("test-topic");
		MessageProducer producer = session.createProducer(topic);
		TextMessage textMessage = session.createTextMessage("it's topic2");
		producer.send(textMessage);
		producer.close();
		session.close();
		connection.close();
	}
	
	//queue接收消息
	@Test
	public void testQueueConsumer() throws Exception{
		//创建一个连接工厂对象
		ConnectionFactory connectionFactory=new ActiveMQConnectionFactory("tcp://47.100.114.233:61616");
		//使用连接工厂对象创建一个连接
		Connection connection = connectionFactory.createConnection();
		//开启连接
		connection.start();
		//使用连接对象创建一个Session对象
		Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
		//使用Session对象创建一个Destination,Destination应该和消息的发送端一致。
		Queue queue = session.createQueue("test-q");
		//使用Session创建一个Consumer对象
		MessageConsumer consumer = session.createConsumer(queue);
		//向Consumer对象中创建一个MessageListener对象,用来接收消息
		consumer.setMessageListener(new MessageListener() {
			//匿名内部类，   new一个接口，相当于写一个接口的实现类,并创建一个对象,类是没名字的
			@Override
			public void onMessage(Message message) {
				//取消息的内容
				 if(message instanceof TextMessage) {
					 TextMessage textMessage=(TextMessage) message;
					 try {
						String text = textMessage.getText();
						//打印消息的内容
						System.out.println(text);
					} catch (JMSException e) {
						e.printStackTrace();
					}
				 }
			}
		});
		//系统等待接收消息
		/*	while(true) {
				Thread.sleep(100);
			}*/
		//系统没接收到键盘输入,系统就一直等待
		System.in.read();
		//关闭资源
		consumer.close();
		session.close();
		connection.close();
		
	}
	
	//topic接收消息
	@Test
	public void testTopicConsumser()throws Exception{
		//创建一个连接工厂对象
				ConnectionFactory connectionFactory=new ActiveMQConnectionFactory("tcp://47.100.114.233:61616");
				//使用连接工厂对象创建一个连接
				Connection connection = connectionFactory.createConnection();
				//开启连接
				connection.start();
				//使用连接对象创建一个Session对象
				Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
				//使用Session对象创建一个Destination,Destination应该和消息的发送端一致。
				//Queue queue = session.createQueue("test-q");
				Topic topic = session.createTopic("test-topic");
				//使用Session创建一个Consumer对象
				MessageConsumer consumer = session.createConsumer(topic);
				//向Consumer对象中创建一个MessageListener对象,用来接收消息
				consumer.setMessageListener(new MessageListener() {
					//匿名内部类，   new一个接口，相当于写一个接口的实现类,并创建一个对象,类是没名字的
					@Override
					public void onMessage(Message message) {
						//取消息的内容
						 if(message instanceof TextMessage) {
							 TextMessage textMessage=(TextMessage) message;
							 try {
								String text = textMessage.getText();
								//打印消息的内容
								System.out.println(text);
							} catch (JMSException e) {
								e.printStackTrace();
							}
						 }
					}
				});
				//系统等待接收消息
				/*	while(true) {
						Thread.sleep(100);
					}*/
				//系统没接收到键盘输入,系统就一直等待
				System.in.read();
				//关闭资源
				consumer.close();
				session.close();
				connection.close();
	}
	
}
