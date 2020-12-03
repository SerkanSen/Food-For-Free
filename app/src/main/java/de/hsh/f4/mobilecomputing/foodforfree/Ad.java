package de.hsh.f4.mobilecomputing.foodforfree;


public class Ad {
    private String title, description, ingredients, pickupLocation, amount, filterOptions, adID, imageUrl;


    public Ad(){
        //empty constructor needed
    }
    public Ad(String testImageUrl){
        imageUrl = testImageUrl;
    }

    public Ad(String title, String description, String ingredients, String amount, String pickupLocation, String filterOptions, String adID, String imageUrl) {
        this.title = title;
        this.description = description;
        this.ingredients = ingredients;
        this.amount = amount;
        this.pickupLocation = pickupLocation;
        this.filterOptions = filterOptions;
        this.adID = adID;
        //Falls imageUrl leer, dann imageUrl = imageUrl(Defaultbild)
        if (imageUrl.trim().equals("")) {
            imageUrl = "https://firebasestorage.googleapis.com/v0/b/food-for-free-9663f.appspot.com/o/ads%2FDefault%20Bild.jpg?alt=media&token=57a564e3-006c-4146-b793-cf4346a8f07a";
        }
        this.imageUrl = imageUrl;
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

    public String getAdID() { return adID; }

    public String getImageUrl() { return imageUrl; }
}
