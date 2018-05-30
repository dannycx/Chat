package com.danny.chat.chat;

/**
 * 消息类
 * Created by danny on 3/27/18.
 */

public class Message {
    public static final int TYPE_RECEIVED = 0;
    public static final int TYPE_SEND = 1;

    private int type;
    private String content;

    public Message(int type, String content) {
        this.type = type;
        this.content = content;
    }

    public int getType() {
        return type;
    }

    public String getContent() {
        return content;
    }
}
