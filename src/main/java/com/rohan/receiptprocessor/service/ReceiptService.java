package com.rohan.receiptprocessor.service;

import com.rohan.receiptprocessor.model.Item;
import com.rohan.receiptprocessor.model.Receipt;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;

@Service
public class ReceiptService {

    private final Map<String, Receipt> receipts = new HashMap<>();
    private static final BigDecimal QUARTER = new BigDecimal("0.25");

    public String processReceipt(Receipt receipt) {
        Objects.requireNonNull(receipt, "Receipt cannot be null");

        String id = UUID.randomUUID().toString();
        receipt.setId(id);
        receipt.setPoints(calculatePoints(receipt));
        receipts.put(id, receipt);
        return id;
    }

    public int getPoints(String id) {
        Receipt receipt = receipts.get(id);
        if (receipt == null) {
            throw new NoSuchElementException("No receipt found for ID: " + id);
        }
        return receipt.getPoints();
    }

    // Rule 1: Points based on the length of retailer's name
    int calculateRetailerNamePoints(String retailer) {
        if (retailer == null || retailer.isEmpty()) return 0;
        return retailer.replaceAll("[^a-zA-Z0-9]", "").length();
    }

    // Rule 2: 50 points if the total is a round dollar amount with no cents
    int calculateRoundDollarAmountPoints(String total) {
        try {
            BigDecimal totalAmount = new BigDecimal(total);
            return totalAmount.stripTrailingZeros().scale() <= 0 ? 50 : 0;
        } catch (NumberFormatException e) {
            return 0; // Return 0 points if the total cannot be parsed
        }
    }

    // Rule 3. 25 points if the total is a multiple of 0.25
    int calculateMultipleOf25Points(String total) {
        try {
            BigDecimal totalAmount = new BigDecimal(total);
            return totalAmount.remainder(QUARTER).compareTo(BigDecimal.ZERO) == 0 ? 25 : 0;
        } catch (NumberFormatException e) {
            return 0; // Return 0 points if the total cannot be parsed
        }
    }

    // Rule 4. 5 points for every two items on the receipt
    int calculateItemPairPoints(int itemCount) {
        return (itemCount / 2) * 5;
    }

    // Rule 5. Points based on item description length
    int calculateItemDescriptionPoints(List<Item> items) {
        if (items == null || items.isEmpty()) return 0;

        int points = 0;
        for (Item item : items) {
            String description = item.getShortDescription();
            if (description != null && !description.trim().isEmpty()) {
                int descriptionLength = description.trim().length();
                if (descriptionLength % 3 == 0) {
                    try {
                        BigDecimal price = new BigDecimal(item.getPrice());
                        points += price.multiply(new BigDecimal("0.2"))
                                .setScale(0, RoundingMode.UP)
                                .intValue();
                    } catch (NumberFormatException e) {
                        // Skip this item if price is not valid
                    }
                }
            }
        }
        return points;
    }

    // Rule 6. 6 points if the day in the purchase date is odd
    int calculateOddDayPoints(String purchaseDate) {
        try {
            LocalDate date = LocalDate.parse(purchaseDate);
            int dayOfMonth = date.getDayOfMonth();
            return (dayOfMonth % 2 != 0) ? 6 : 0;
        } catch (DateTimeParseException e) {
            return 0; // Return 0 points if the date is invalid
        }
    }

    // Rule 7. 10 points if the time of purchase is after 2:00pm and before 4:00pm
    int calculatePurchaseTimePoints(String purchaseTime) {
        try {
            LocalTime time = LocalTime.parse(purchaseTime, DateTimeFormatter.ofPattern("HH:mm"));
            return (time.isAfter(LocalTime.of(14, 0)) && time.isBefore(LocalTime.of(16, 0))) ? 10 : 0;
        } catch (DateTimeParseException e) {
            return 0; // Return 0 points if the time cannot be parsed
        }
    }

    private int calculatePoints(Receipt receipt) {
        int points = 0;
        points += calculateRetailerNamePoints(receipt.getRetailer());
        points += calculateRoundDollarAmountPoints(receipt.getTotal());
        points += calculateMultipleOf25Points(receipt.getTotal());
        points += calculateItemPairPoints(receipt.getItems().size());
        points += calculateItemDescriptionPoints(receipt.getItems());
        points += calculateOddDayPoints(receipt.getPurchaseDate());
        points += calculatePurchaseTimePoints(receipt.getPurchaseTime());
        return points;
    }
}
