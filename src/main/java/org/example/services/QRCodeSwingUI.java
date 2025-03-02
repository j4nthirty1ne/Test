package org.example.services;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Timer;
import java.util.TimerTask;

public class QRCodeSwingUI extends JFrame {
    private int transactionId;
    private PaymentService paymentService;
    private JLabel statusLabel;

    public QRCodeSwingUI(int transactionId, BufferedImage qrImage) {
        this.transactionId = transactionId;
        this.paymentService = new PaymentService();

        // Setup UI
        setTitle("Scan QR Code to Pay");
        setSize(400, 450);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        // Display QR Code
        JLabel qrLabel = new JLabel(new ImageIcon(qrImage));
        add(qrLabel, BorderLayout.CENTER);

        // Status Label (to show payment status)
        statusLabel = new JLabel("Waiting for payment...", SwingConstants.CENTER);
        statusLabel.setFont(new Font("Arial", Font.BOLD, 14));
        add(statusLabel, BorderLayout.SOUTH);

        // Center Window
        setLocationRelativeTo(null);
        setVisible(true);

        // Start Checking Payment Status
        checkPaymentStatus();
    }

    private void checkPaymentStatus() {
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                boolean isPaid = paymentService.checkPaymentStatus(transactionId);

                if (isPaid) {
                    statusLabel.setText("✅ Payment Successful!");
                    JOptionPane.showMessageDialog(null, "Payment Confirmed!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    dispose(); // Close the window
                    timer.cancel();
                }
            }
        }, 2000, 5000); // Check every 5 seconds
    }
}
