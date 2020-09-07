package com.alun.mq;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class MqConfig {
    @Autowired
    private CachingConnectionFactory connectionFactory;

    @Bean
    public RabbitTemplate rabbitTemplate() {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(messageConverter());

        // 消息是否成功发送到Exchange
        rabbitTemplate.setConfirmCallback((correlationData, ack, cause) -> {
            if (ack) {
                Message message = (Message) correlationData;
                log.info("消息成功发送: {},{}", message.getData(), message.getId());
            } else {
                log.info("消息发送到Exchange失败, {}, cause: {}", correlationData, cause);
            }
        });

        // 触发setReturnCallback回调必须设置mandatory=true, 否则Exchange没有找到Queue就会丢弃掉消息, 而不会触发回调
        rabbitTemplate.setMandatory(true);
        // 消息是否从Exchange路由到Queue, 注意: 这是一个失败回调, 只有消息从Exchange路由到Queue失败才会回调这个方法
        rabbitTemplate.setReturnCallback((message, replyCode, replyText, exchange, routingKey) -> {
            log.info("消息从Exchange路由到Queue失败: exchange: {}, route: {}, replyCode: {}, replyText: {}, message: {}", exchange, routingKey, replyCode, replyText, message);
        });

        return rabbitTemplate;
    }

    @Bean
    public SimpleMessageListenerContainer messageContainer() {

        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer(connectionFactory);
        MessageListenerAdapter adapter = new MessageListenerAdapter();
        adapter.setMessageConverter(messageConverter());
        container.setMessageListener(adapter);
        return container;

    }


    /**
     * 消息转换器(toJSON)
     *
     * @return
     */

    @Bean
    public Jackson2JsonMessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }


    @Bean
    public Queue mailQueue() {
        // 持久化,非排他,非自动删除队列
        return new Queue("mail.queue", true, false, false);
    }

    @Bean
    public TopicExchange topicExchange() {
        // 定义一个名为topicExchange的topic交换器
        return new TopicExchange("mail.exchange", true, false);
    }

    @Bean
    public Binding bindingTopicExchangeWithA() {
        // 关系绑定
        return BindingBuilder.bind(mailQueue()).to(topicExchange()).with("mail.routing.key");
    }

}
