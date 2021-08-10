package com.example.chatty_2.Models;

public class Messages {
    String Uid // Uid of the person sending the message
            , message,messageId;
    Long timeStamp;

    public Messages(String uid, String message, Long timeStamp) {
        Uid = uid;
        this.message = message;
        this.timeStamp = timeStamp;
    }
    public Messages(String uid, String message) {
        Uid = uid;
        this.message = message;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public Messages() {}

    public String getUid() {
        return Uid;
    }

    public void setUid(String uid) {
        Uid = uid;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(Long timeStamp) {
        this.timeStamp = timeStamp;
    }
}
