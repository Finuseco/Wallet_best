# Implementation Plan: Enhancing CashPay All PayScan

## Overview
The goal is to enhance the existing `QrScannerDialog` in the CashPay All Android app to support three distinct modes of operation for all users:
1. Product Scanning: Scan barcodes to add items to a basket, calculate totals in real-time, and facilitate payment.
2. Payment Request Scanning: Scan a payment request to pay directly.
3. Wallet/Transfer Scanning: Scan a wallet ID or QR code to send money.

## High-Level Tasks
1. Update `AuthUiState` and `DashboardViewModel` to include state for the new scan modes (e.g., basket, current total, exchange rates).
2. Enhance `QrScannerDialog` to distinguish between the three scan modes based on the scanned data payload.
3. Implement the product basket UI and logic, including real-time total calculation.
4. Integrate API endpoints:
    - `/api/v1/scanpay/product` for product validation.
    - `/api/v1/scanpay` for POS payment processing.
5. Implement exchange rate logic for multi-currency support in the basket.
6. Ensure secure handling of transactions and PIN authentication.

## Security & Compliance
- PIN will be required for payment confirmation.
- No storage of PINs.
- HTTPS communication.
- No automatic payments upon scan (confirmation required).
- Validation and processing occur strictly on the backend.
