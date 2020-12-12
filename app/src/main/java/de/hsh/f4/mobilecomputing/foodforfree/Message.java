package de.hsh.f4.mobilecomputing.foodforfree;

public class Message {
    private String interestedUser;
    private String offeringUser;
    private String sender, senderName;
    private String message;
    private String timestamp, imageUrl, adTitle;

    public Message(){
        //empty constructor needed
    }

    public Message(String interestedUser, String offeringUser, String sender, String senderName, String message, String timestamp, String imageUrl, String adTitle){
        this.interestedUser = interestedUser;
        this.offeringUser =offeringUser;
        this.message = message;
        this.timestamp = timestamp;
        this.imageUrl = imageUrl;
        this.adTitle = adTitle;
        this.sender = sender;
        this.senderName = senderName;

    }

    public String getInterestedUser() {
        return interestedUser;
    }

    public String getOfferingUser() {
        return offeringUser;
    }

    public String getSender() {
        return sender;
    }

    public String getSenderName() {return senderName; }

    public String getMessage() {
        return message;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getAdTitle() {
        return adTitle;
    }
}
