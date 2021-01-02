//Dieser Code wurde erstellt von Laura Nguyen
package de.hsh.f4.mobilecomputing.foodforfree;


import java.util.List;

public class Ad {
    private String title, description, ingredients, pickupLocation, amount, adID, userID, imageUrl, filterOptions, timestamp;
    private List<String> categories;

    public Ad(){
        //empty constructor needed
    }

    public Ad(String title, String description, String ingredients, String amount, String pickupLocation, String adID, String userID, String imageUrl, String filterOptions, List<String> categories, String timestamp) {
        this.title = title;
        this.description = description;
        this.ingredients = ingredients;
        this.amount = amount;
        this.pickupLocation = pickupLocation;
        this.filterOptions = filterOptions;
        this.categories = categories;
        this.adID = adID;
        this.userID = userID;
        this.imageUrl = imageUrl;
        this.timestamp = timestamp;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getIngredients() {
        return ingredients;
    }

    public String getPickupLocation() {
        return pickupLocation;
    }

    public String getAmount() {
        return amount;
    }

    public String getFilterOptions() {
        return filterOptions;
    }

    public List<String> getCategories() { return categories; }

    public String getAdID() {
        return adID;
    }

    public String getUserID() {
        return userID;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getTimestamp() {
        return timestamp;
    }
}
