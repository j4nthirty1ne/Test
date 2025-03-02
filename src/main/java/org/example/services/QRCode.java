package org.example.services;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import kh.gov.nbc.bakong_khqr.BakongKHQR;
import kh.gov.nbc.bakong_khqr.model.*;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class QRCode<KHQRTransactionStatus> {
    private static final String BAKONG_CHECK_URL = "https://api-bakong.nbc.gov.kh/v1/check_transaction_by_md5";
    private JFrame frame;
    private JLabel statusLabel;

    public void generateAndDisplayQRCode() throws WriterException {
        IndividualInfo individualInfo = new IndividualInfo();
        individualInfo.setAccountInformation("010513288");
        individualInfo.setBakongAccountId("sreng_chipor@aclb");
        individualInfo.setAcquiringBank("ABA");
        individualInfo.setCurrency(KHQRCurrency.USD);
        individualInfo.setAmount(0.01);
        individualInfo.setMerchantName("ROS Cambodia");
        individualInfo.setMerchantCity("Phnom Penh");

        KHQRResponse<KHQRData> response = BakongKHQR.generateIndividual(individualInfo);

        if (response.getKHQRStatus().getCode() == 0) {
            String qrText = response.getData().getQr();
            String md5 = response.getData().getMd5();

            if (qrText == null || qrText.isEmpty()) {
                System.out.println("⚠️ Error: QR Code Data is empty!");
                return;
            }

            BufferedImage qrImage = generateQRImage(qrText, 300, 300);
            displayQRPopup(qrImage, md5);
        } else {
            System.out.println("\n❌ Error: " + response.getKHQRStatus().getMessage());
        }
    }

    private BufferedImage generateQRImage(String text, int width, int height) throws WriterException {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, width, height);
        return MatrixToImageWriter.toBufferedImage(bitMatrix);
    }

    private void displayQRPopup(BufferedImage image, String md5) {
        frame = new JFrame("Scan QR Code");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        JLabel qrLabel = new JLabel(new ImageIcon(image));
        statusLabel = new JLabel("Waiting for Payment...", SwingConstants.CENTER);
        statusLabel.setFont(new Font("Arial", Font.BOLD, 14));

        frame.add(qrLabel, BorderLayout.CENTER);
        frame.add(statusLabel, BorderLayout.SOUTH);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        new Thread(() -> waitForPayment(md5)).start();
    }

    private void waitForPayment(String md5) {
        int attempts = 0;
        while (attempts < 30) {
            try {
                Thread.sleep(3000);
                KHQRResponse<KHQRTransactionStatus> response = BakongKHQR.checkTransactionByMd5(md5);
                if (response.getKHQRStatus().getCode() == 0 && "SUCCESS".equals(response.getData().getStatus())) {
                    SwingUtilities.invokeLater(() -> {
                        statusLabel.setText("Payment Successful!");
                        JOptionPane.showMessageDialog(frame, "Payment Successful!", "Success", JOptionPane.INFORMATION_MESSAGE);
                        frame.dispose();
                    });
                    return;
                }
            } catch (InterruptedException | IOException e) {
                e.printStackTrace();
            }
            attempts++;
        }
        SwingUtilities.invokeLater(() -> {
            statusLabel.setText("Payment Failed or Timed Out");
            JOptionPane.showMessageDialog(frame, "Payment Failed or Timed Out", "Error", JOptionPane.ERROR_MESSAGE);
            frame.dispose();
        });
    }
}