package com.example.kidbank.kidbanknew.Model;

import android.graphics.Bitmap;

/**
 * Created by vishal on 22/3/18.
 */

public class MoneyGridData {


    String money_id;
    String amount;

    public String getMoney_id() {
        return money_id;
    }

    public void setMoney_id(String money_id) {
        this.money_id = money_id;
    }

    public MoneyGridData(String amount) {
        super();
        this.amount = amount;

    }

    public MoneyGridData() {

    }

    public String getTitle() {
        return amount;
    }
    public void setTitle(String title) {
        this.amount = title;
    }
}
