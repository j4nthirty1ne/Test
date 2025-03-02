package org.example.services;

import org.json.JSONObject;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

public class PaymentService {
    private static final String BAKONG_API_URL = "https://api-bakong.nbc.gov.kh/v1/check_transaction_by_md5";

    public int initiatePayment(int orderId, double amount) {
        // Normally, the KHQR API generates a payment request and returns a transaction ID.
        return (int) (Math.random() * 100000); // Simulating a transaction ID
    }

    public String getKHQR(int transactionId) {
        // Normally, fetch KHQR data from your system
        return "KHQR-PAYMENT-CODE-DATA"; // Replace with actual QR string
    }

    public boolean checkPaymentStatus(int transactionId) {
        try {
            URL url = new URL(BAKONG_API_URL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            JSONObject requestData = new JSONObject();
            requestData.put("transactionId", transactionId);

            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = requestData.toString().getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            int responseCode = conn.getResponseCode();
            if (responseCode == 200) {
                try (BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "utf-8"))) {
                    StringBuilder response = new StringBuilder();
                    String responseLine;
                    while ((responseLine = br.readLine()) != null) {
                        response.append(responseLine.trim());
                    }
                    JSONObject jsonResponse = new JSONObject(response.toString());
                    return jsonResponse.getBoolean("paid");
                }
            } else {
                System.out.println("Error checking payment status: " + responseCode);
            }
        } catch (Exception e) {
            System.out.println("Payment status check failed: " + e.getMessage());
        }
        return false;
    }
}
