package com.example.diabeteshealthmonitoringapplication.models;

public class Chat {
    private String fromUid,toUid,message,fromName,toName;
    private Long time;

    /**
     * @NoArgsConstructor
     */
    public Chat() {

    }

    /**
     * @AllArgsConstructor
     * @param fromUid
     * @param toUid
     * @param message
     * @param time
     */
    public Chat(String fromUid, String toUid, String message,Long time,String fromName,String toName) {
        this.fromUid = fromUid;
        this.toUid = toUid;
        this.message = message;
        this.time = time;
        this.fromName = fromName;
        this.toName = toName;
    }

    public String getFromUid() {
        return fromUid;
    }

    public void setFromUid(String fromUid) {
        this.fromUid = fromUid;
    }

    public String getToUid() {
        return toUid;
    }

    public void setToUid(String toUid) {
        this.toUid = toUid;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }

    public String getFromName() {
        return fromName;
    }

    public void setFromName(String fromName) {
        this.fromName = fromName;
    }

    public String getToName() {
        return toName;
    }

    public void setToName(String toName) {
        this.toName = toName;
    }
}
