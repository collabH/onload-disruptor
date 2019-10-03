package dev.disruptor.common;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 * @author echo huang
 * @version 1.0
 * @date 2019-10-03 19:43
 * @description
 */
@Data
@ToString
public class TranslatorData implements Serializable {
    private String id;
    private String name;
    //传输消息体内容
    private String message;
}
