package com.jeff.fischman.exercise.messages;

public interface Message {
    MsgType getMsgType();
    void invokeHandlerMethod(MessageHandler messageHandler);
}
