package com.ecarezone.android.patient.model;

import android.renderscript.Element;

import com.google.gson.annotations.Expose;
import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;

import java.io.File;
import java.io.Serializable;
import java.util.Date;

/**
 * Created by L&T Technology Services
 */
public class Chat implements Serializable {
    @Expose
    @DatabaseField(canBeNull = false)
    private String chatUserId;

    @Expose
    @DatabaseField(canBeNull = true)
    private String outGoingImageUrl;

    @Expose
    @DatabaseField(canBeNull = true)
    private String inComingImageUrl;

    @Expose
    @DatabaseField(canBeNull = true)
    private String deviceImagePath;

    @Expose
    @DatabaseField(canBeNull = true)
    private String messageText;

    @Expose
    @DatabaseField(canBeNull = true, dataType = DataType.DATE_STRING,
            format = "yyyy-MM-dd HH:mm:ss")
    private Date timeStamp;

    @Expose
    @DatabaseField(canBeNull = true)
    private String readStatus;

    @Expose
    @DatabaseField(canBeNull = true)
    private String deliveryStatus;

    @Expose
    @DatabaseField(canBeNull = true)
    private String chatType;

    private File discImageFile;
    private String senderId;
    private String receiverId;
    private boolean isChatSending;


    public Chat() {

    }

    public boolean isChatSending() {
        return isChatSending;
    }

    public void setIsChatSending(boolean isChatSending) {
        this.isChatSending = isChatSending;
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

    public String getReadStatus() {
        return readStatus;
    }

    public void setReadStatus(String readStatus) {
        this.readStatus = readStatus;
    }

    public String getChatUserId() {
        return chatUserId;
    }

    public void setChatUserId(String chatUserId) {
        this.chatUserId = chatUserId;
    }

    public String getDeliveryStatus() {
        return deliveryStatus;
    }

    public void setDeliveryStatus(String deliveryStatus) {
        this.deliveryStatus = deliveryStatus;
    }

    public String getChatType() {
        return chatType;
    }

    public void setChatType(String chartType) {
        this.chatType = chartType;
    }
}