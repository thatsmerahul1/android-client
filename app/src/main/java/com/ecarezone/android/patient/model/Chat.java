package com.ecarezone.android.patient.model;

import java.io.File;
import java.util.Date;

/**
 * Created by L&T Technology Services
 */
public class Chat {
    private String chatId;
    private String outGoingImageUrl;
    private String inComingImageUrl;
    private String deviceImagePath;
    private String senderId;
    private String receiverId;
    private String messageText;
    private Date timeStamp;

    private File discImageFile;

    public String getChatId() {
        return chatId;
    }

    public void setChatId(String chatId) {
        this.chatId = chatId;
    }

    public Date getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(Date timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(String receiverId) {
        this.receiverId = receiverId;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getMessageText() {
        return messageText;
    }

    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }

    public String getDeviceImagePath() {
        return deviceImagePath;
    }

    public void setDeviceImagePath(String deviceImagePath) {
        this.deviceImagePath = deviceImagePath;
    }

    public String getInComingImageUrl() {
        return inComingImageUrl;
    }

    public void setInComingImageUrl(String inComingImageUrl) {
        this.inComingImageUrl = inComingImageUrl;
    }

    public String getOutGoingImageUrl() {
        return outGoingImageUrl;
    }

    public void setOutGoingImageUrl(String outGoingImageUrl) {
        this.outGoingImageUrl = outGoingImageUrl;
    }

    public File getDiscImageFile() {
        return discImageFile;
    }

    public void setDiscImageFile(File discImageFile) {
        this.discImageFile = discImageFile;
    }
}