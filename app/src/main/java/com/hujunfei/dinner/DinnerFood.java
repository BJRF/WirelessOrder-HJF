package com.hujunfei.dinner;

import java.io.Serializable;


public class DinnerFood implements Serializable {

    private static final String TAG = "DINNERFOOD";

    // 菜名

    public String GetFoodName() {
        return food_name;
    }

    public void SetFoodName(String foodname) {
        this.food_name = foodname;
    }

    public String GetFoodType() {
        return food_type;
    }

    public void SetFoodType(String foodtype) {
        this.food_type = foodtype;
    }

    public int GetFoodPrice() {
        return food_price;
    }

    public void SetFoodPrice(int foodprice) {
        this.food_price = foodprice;
    }

    private String food_name = "";

    // 单价
    private int food_price = 0;

    // 类别
    private String food_type = "";

    public DinnerFood() {
        super();
    }

    public DinnerFood(String foodname, int foodprice, String food_type) {
        super();
        this.food_name = foodname;
        this.food_price = foodprice;
        this.food_type = food_type;
    }
}
