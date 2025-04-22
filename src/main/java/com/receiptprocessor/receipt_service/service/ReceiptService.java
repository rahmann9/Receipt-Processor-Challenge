package com.receiptprocessor.receipt_service.service;

import com.receiptprocessor.receipt_service.model.Item;
import com.receiptprocessor.receipt_service.model.Receipt;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class ReceiptService {
    private final Map<String, Receipt> receipts = new ConcurrentHashMap<>();
    private final Map<String, Integer> receiptPoints = new ConcurrentHashMap<>();

    public String processReceipt(Receipt receipt) {
        // Generate unique Id to connect receipt and receipt point and to GET points
        String id = UUID.randomUUID().toString();
        receipts.put(id, receipt);
        int points = calculatePoints(receipt);
        receiptPoints.put(id, points);
        return id;
    }

    public Integer getPoints(String id) {
        return receiptPoints.get(id);
    }

    public boolean receiptExists(String id) {
        return receipts.containsKey(id);
    }

    private int calculatePoints(Receipt receipt) {
        int points = 0;

        // Rule 1: One point for every alphanumeric character in the retailer name
        String retailer = receipt.getRetailer();
        // Goes through each letter and increments points in the character is alphanumerical (Memory efficient)
        for (int i = 0, length = retailer.length(); i < length; i++) {
            char c = retailer.charAt(i);
            if ((c >= '0' && c <= '9') || (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z')){
                points++;
            }
        }

        // Rule 2: 50 points if the total is a round dollar amount with no cents
        Double total = Double.parseDouble(receipt.getTotal());
        // Checks if the number is a whole number (a round dollar amount with no cents)
        if (total % 1 == 0 && total >= 1.0) {points += 50;}

        // Rule 3: 25 points if total is a multiple of 0.25
        if ((total * 100) % 25 == 0 && total > 0) {points += 25;}

        // Rule 4: 5 points for every two items on the receipt
        int itemCount = receipt.getItems().size();
        points += (itemCount/2) * 5;

        // Rule 5: If trimmed description length is a multiple of 3, multiply price by 0.2 and round up to nearest integer
        for(Item item : receipt.getItems()) {
            String trimmedDescription = item.getShortDescription().trim();
            if (trimmedDescription.length() % 3 == 0) {
                double price = Double.parseDouble(item.getPrice());
                points += (int) Math.ceil(price * 0.2);
            }
        }

        // Rule 6: 6 points if purchase day is odd
        String[] date = receipt.getPurchaseDate().split("-"); // YYYY-MM-DD
        if (date.length == 3) {
            int day = Integer.parseInt(date[2]);
            if (day % 2 != 0) {
                points += 6;
            }
        }

        // Rule 7: 10 points if purchase time is after 2:00pm and before 4:00pm
        String[] time = receipt.getPurchaseTime().split(":"); // HH:mm
        if (time.length == 2) {
            int hour = Integer.parseInt(time[0]);
            int minute = Integer.parseInt(time[1]);
            // This will exclude exactly 2:00pm (14:00) and before, but include 3:00pm to 3:59pm (15:00–15:59)
            if ((hour == 14 && minute > 0) || (hour == 15)) {
                points += 10;
            }
        }
        return points;
    }
}
