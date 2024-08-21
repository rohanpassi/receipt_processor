package com.rohan.receiptprocessor.model;

import lombok.Data;

import java.util.List;

@Data
public class Receipt {
    String id;
    String retailer;
    String purchaseDate;
    String purchaseTime;
    List<Item> items;
    String total;
    Integer points;
}
