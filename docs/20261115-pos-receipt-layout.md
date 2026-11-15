# POS receipt alignment

The shared PosReceipt bitmap renderer retains 384 px / 576 px thermal widths (58 / 80 mm). Logo and receipt title precede the Code 128 barcode and readable transaction reference. Operation/status, transaction metadata and monetary totals use two columns with independent wrapping and row height determined by the taller cell. ISO transaction dates are formatted with a French weekday, calendar date and local time; unknown formats retain their original value.

The bottom container places the QR reference at the left and the electronic receipt/support text to its right. The container reserves the taller side's height, avoiding overlap. QR and Code 128 encode only the transaction reference, never a payment request. Failed/pending payments retain the explicit non-confirmation message. PDF, preview, Android printing and ESC/POS all use this same bitmap layout. No financial calculations or stored data changed.

Validation: Kotlin compilation passed. The native Robolectric PDF/screenshot test could not run because its test dependencies (including Robolectric 4.16.1) are absent from the offline Gradle cache. No physical printer or Android screenshot verification was performed in this change.
