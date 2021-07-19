package com.example.diabeteshealthmonitoringapplication.models;

import java.util.List;

public class ChatListItem {
    private String toName,fromName,toUid,fromUid,fromImageUrl,toImageUrl;
    private List<Chat> chats;

    /**
     * @NoArgsConstructor
     */
    public ChatListItem() {

    }

    /**
     * @AllArgsConstructor
     * @param toName
     * @param fromName
     * @param toUid
     * @param fromUid
     * @param chats
     * @param fromImageUrl
     * @param toImageUrl
     */
    public ChatListItem(String toName, String fromName, String toUid, String fromUid, List<Chat> chats,String fromImageUrl,String toImageUrl) {
        this.toName = toName;
        this.fromName = fromName;
        this.toUid = toUid;
        this.fromUid = fromUid;
        this.chats = chats;
        this.fromImageUrl = fromImageUrl;
        this.toImageUrl = toImageUrl;
    }

    public String getToName() {
        return toName;
    }

    public void setToName(String toName) {
        this.toName = toName;
    }

    public String getFromName() {
        return fromName;
    }

    public void setFromName(String fromName) {
        this.fromName = fromName;
    }

    public String getToUid() {
        return toUid;
    }

    public void setToUid(String toUid) {
        this.toUid = toUid;
    }

    public String getFromUid() {
        return fromUid;
    }

    public void setFromUid(String fromUid) {
        this.fromUid = fromUid;
    }

    public List<Chat> getChats() {
        return chats;
    }

    public void setChats(List<Chat> chats) {
        this.chats = chats;
    }

    public String getFromImageUrl() {
        return fromImageUrl;
    }

    public void setFromImageUrl(String fromImageUrl) {
        this.fromImageUrl = fromImageUrl;
    }

    public String getToImageUrl() {
        return toImageUrl;
    }

    public void setToImageUrl(String toImageUrl) {
        this.toImageUrl = toImageUrl;
    }
}
