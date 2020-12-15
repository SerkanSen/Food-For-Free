package de.hsh.f4.mobilecomputing.foodforfree;

public class Message {
    private String interestedUser, interestedUserID;
    private String offeringUser, offeringUserID;
    private String sender, senderName;
    private String message, lastMessage;
    private String timestamp, lastTimestamp, imageUrl, adTitle;

    public Message(){
        //empty constructor needed
    }

    public Message(String interestedUser, String interestedUserID, String offeringUser, String offeringUserID, String sender, String senderName, String message, String lastMessage, String timestamp, String lastTimestamp, String imageUrl, String adTitle){
        this.interestedUser = interestedUser;
        this.interestedUserID = interestedUserID;
        this.offeringUser = offeringUser;
        this.offeringUserID = offeringUserID;
        this.message = message;
        this.lastMessage = lastMessage;
        this.timestamp = timestamp;
        this.lastTimestamp = lastTimestamp;
        this.imageUrl = imageUrl;
        this.adTitle = adTitle;
        this.sender = sender;
        this.senderName = senderName;

    }

    public String getInterestedUser() {
        return interestedUser;
    }

    public String getInterestedUserID() { return interestedUserID; }

    public String getOfferingUser() {
        return offeringUser;
    }

    public String getOfferingUserID() { return offeringUserID; }

    public String getSender() {
        return sender;
    }

    public String getSenderName() {return senderName; }

    public String getMessage() {
        return message;
    }

    public String getLastMessage() { return lastMessage; }

    public String getTimestamp() {
        return timestamp;
    }

    public String getLastTimestamp() { return lastTimestamp; }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getAdTitle() {
        return adTitle;
    }
}
