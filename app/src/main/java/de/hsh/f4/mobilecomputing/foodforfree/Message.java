//Dieser Code wurde erstellt von Laura Nguyen
package de.hsh.f4.mobilecomputing.foodforfree;

public class Message {
    private String interestedUser, interestedUserID;
    private String offeringUser, offeringUserID;
    private String sender, senderName;
    private String message, lastMessage;
    private String timestamp, lastTimestamp, messageTime, imageUrl, adTitle, adID;

    public Message(){
        //empty constructor needed
    }

    public Message(String interestedUser, String interestedUserID, String offeringUser, String offeringUserID, String sender, String senderName,
                   String message, String lastMessage, String timestamp, String lastTimestamp, String messageTime, String imageUrl, String adTitle, String adID){
        this.interestedUser = interestedUser;
        this.interestedUserID = interestedUserID;
        this.offeringUser = offeringUser;
        this.offeringUserID = offeringUserID;
        this.message = message;
        this.lastMessage = lastMessage;
        this.timestamp = timestamp;
        this.lastTimestamp = lastTimestamp;
        this.messageTime = messageTime;
        this.imageUrl = imageUrl;
        this.adTitle = adTitle;
        this.adID = adID;
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

    public String getMessageTime() {
        return messageTime;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getAdTitle() {
        return adTitle;
    }

    public String getAdID() { return  adID;}
}
