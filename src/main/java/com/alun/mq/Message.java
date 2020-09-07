package com.alun.mq;

import org.springframework.amqp.rabbit.connection.CorrelationData;

public class Message extends CorrelationData {

    private Object data;

    public Message(String id, Object data) {
        super(id);
        this.data = data;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }


}
