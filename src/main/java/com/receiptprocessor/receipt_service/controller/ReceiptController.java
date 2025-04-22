package com.receiptprocessor.receipt_service.controller;

import com.receiptprocessor.receipt_service.dto.PointsResponse;
import com.receiptprocessor.receipt_service.model.Receipt;
import com.receiptprocessor.receipt_service.dto.ReceiptIdResponse;
import com.receiptprocessor.receipt_service.service.ReceiptService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/receipt")
public class ReceiptController {
    private ReceiptService receiptService;

    @Autowired
    public ReceiptController(ReceiptService receiptService) {
        this.receiptService = receiptService;
    }

    @RequestMapping("/points")
    public ResponseEntity<ReceiptIdResponse>processReceipt(@RequestBody Receipt receipt) {
        String id = receiptService.processReceipt(receipt);
        return ResponseEntity.ok(new ReceiptIdResponse(id));
    }

    @GetMapping("/{id}/points")
    public ResponseEntity<PointsResponse> getPoints(@PathVariable String id) {
        if (!receiptService.receiptExists(id)) {
            return ResponseEntity.notFound().build();
        }
        Integer points = receiptService.getPoints(id);
        return ResponseEntity.ok(new PointsResponse(points));
    }
}
