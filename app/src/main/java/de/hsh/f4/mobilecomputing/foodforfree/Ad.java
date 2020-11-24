package de.hsh.f4.mobilecomputing.foodforfree;

import java.util.List;

public class Ad {
    private String title, description, ingredients, pickupLocation, amount, filterOptions;
    //List<String> filterOptions;


    public Ad(){
        //empty constructor needed
    }

    public Ad(String title, String description, String ingredients, String amount, String pickupLocation, String filterOptions) {
        this.title = title;
        this.description = description;
        this.ingredients = ingredients;
        this.amount = amount;
        this.pickupLocation = pickupLocation;
        this.filterOptions = filterOptions;
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
}
