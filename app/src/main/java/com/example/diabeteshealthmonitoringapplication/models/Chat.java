package com.example.diabeteshealthmonitoringapplication.models;

public class Chat {
    private String fromUid,toUid,message;
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
    public Chat(String fromUid, String toUid, String message,Long time) {
        this.fromUid = fromUid;
        this.toUid = toUid;
        this.message = message;
        this.time = time;
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
}
