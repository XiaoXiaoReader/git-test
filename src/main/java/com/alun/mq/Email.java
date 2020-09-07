package com.alun.mq;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Accessors(chain = true)
@NoArgsConstructor
public class Email implements Serializable {

    private static final long serialVersionUID = 1L;
    private String id;

    private String sender;

    private String email; //接收方邮件,多个逗号分隔


    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    @JsonDeserialize(using = LocalDateTimeDeserializer.class) //解决mq序列化
    @JsonSerialize(using = LocalDateTimeSerializer.class) //解决mq序列化
    private LocalDateTime sendTime;

    private String template; //模板

    private String stats; //状态，0：未投递，1投递成功，2发送成功，3投递失败
}