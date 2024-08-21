package com.rohan.receiptprocessor.service;

import com.rohan.receiptprocessor.model.Item;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ReceiptServiceTest {
    private ReceiptService service;
    @BeforeEach
    void setUp() {
        service = new ReceiptService();
    }
    @AfterEach
    void tearDown() {
        // No-op for now
    }

    @Test
    void testCalculateRetailerNamePoints() {
        assertEquals(0, service.calculateRetailerNamePoints(""));
        assertEquals(10, service.calculateRetailerNamePoints("HelloWorld"));
        assertEquals(8, service.calculateRetailerNamePoints("12345678"));
        assertEquals(13, service.calculateRetailerNamePoints("Hello123World"));
        assertEquals(10, service.calculateRetailerNamePoints("H3ll0!W0rld"));
        assertEquals(6, service.calculateRetailerNamePoints("Target"));
        assertEquals(14, service.calculateRetailerNamePoints("M&M Corner Market"));
        assertEquals(0, service.calculateRetailerNamePoints(null), "Null retailer should return 0 points");
    }

    @Test
    void testCalculateRoundDollarAmountPoints() {
        assertEquals(50, service.calculateRoundDollarAmountPoints("123"));
        assertEquals(50, service.calculateRoundDollarAmountPoints("45.000"));
        assertEquals(0, service.calculateRoundDollarAmountPoints("78.95"));
        assertEquals(50, service.calculateRoundDollarAmountPoints("67.0"));
        assertEquals(0, service.calculateRoundDollarAmountPoints("invalid"), "Invalid total should return 0 points");
    }

    @Test
    void testCalculateMultipleOf25Points() {
        assertEquals(25, service.calculateMultipleOf25Points("10.25"), "Total is a multiple of 0.25, should return 25 points");
        assertEquals(25, service.calculateMultipleOf25Points("5.00"), "Total is a multiple of 0.25, should return 25 points");
        assertEquals(0, service.calculateMultipleOf25Points("6.49"), "Total is not a multiple of 0.25, should return 0 points");
    }

    @Test
    void testCalculateItemPairPoints() {
        Item item1 = new Item();
        item1.setShortDescription("Item 1");
        item1.setPrice("1.00");

        Item item2 = new Item();
        item2.setShortDescription("Item 2");
        item2.setPrice("2.00");

        Item item3 = new Item();
        item3.setShortDescription("Item 3");
        item3.setPrice("3.00");

        assertEquals(5, service.calculateItemPairPoints(List.of(item1, item2).size()), "2 items should return 5 points");
        assertEquals(5, service.calculateItemPairPoints(List.of(item1, item2, item3).size()), "3 items should return 5 points");
        assertEquals(10, service.calculateItemPairPoints(List.of(item1, item2, item3, item1).size()), "4 items should return 10 points");
    }

    @Test
    void testCalculateItemDescriptionPoints() {
        Item item1 = new Item();
        item1.setShortDescription("Item1Desc"); // Length 9, multiple of 3
        item1.setPrice("6.49");

        Item item2 = new Item();
        item2.setShortDescription("Item2"); // Length 5, not a multiple of 3
        item2.setPrice("2.00");

        assertEquals(2, service.calculateItemDescriptionPoints(List.of(item1)), "Item description length is a multiple of 3, should return 2 points");
        assertEquals(0, service.calculateItemDescriptionPoints(List.of(item2)), "Item description length is not a multiple of 3, should return 0 points");
        assertEquals(2, service.calculateItemDescriptionPoints(List.of(item1, item2)), "Only one item qualifies for description points, should return 2 points");
    }

    @Test
    void testCalculateOddDayPoints() {
        assertEquals(6, service.calculateOddDayPoints("2022-01-01"), "Day is odd, should return 6 points");
        assertEquals(0, service.calculateOddDayPoints("2022-01-02"), "Day is even, should return 0 points");
        assertEquals(0, service.calculateOddDayPoints("invalid-date"), "Invalid date should return 0 points");
    }

    @Test
    void testCalculatePurchaseTimePoints() {
        assertEquals(10, service.calculatePurchaseTimePoints("14:30"), "Time is between 2:00 PM and 4:00 PM, should return 10 points");
        assertEquals(0, service.calculatePurchaseTimePoints("13:30"), "Time is before 2:00 PM, should return 0 points");
        assertEquals(0, service.calculatePurchaseTimePoints("16:01"), "Time is after 4:00 PM, should return 0 points");
        assertEquals(0, service.calculatePurchaseTimePoints("14:00"), "Time is exactly 2:00 PM, should return 0 points");
        assertEquals(0, service.calculatePurchaseTimePoints("16:00"), "Time is exactly 4:00 PM, should return 0 points");
    }
}