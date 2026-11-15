# Approval-based loan calendar — Android 12.8

LoanDto now reads request_date and approval_date separately from start/end. Pending history shows the request timestamp and states that an installment schedule starts after approval; rejected/cancelled requests show no active schedule. A pending status is labelled 'En attente d’approbation'. No request-date fallback is presented as an approval date.

Approved loan/history details and installment due dates use the full timestamp formatter: weekday, full French date, HH:mm:ss and timezone. ISO instants are converted to the displayed device timezone; null values remain unknown and date-only legacy data has no invented time. No installment calculation takes place on the phone.

The CashPay Web update supplies full ISO timestamps and anchors schedule creation to its database-recorded approval instant. Install the backend update as well for existing APIs that previously truncated timestamps to dates. Android version name 12.8 / version code 128.

Validation: pure JUnit tests passed for Monday approval/weekly expiry, exact hours, UTC+2 conversion, midnight weekday rollover, absent approval and date-only legacy data. Kotlin compilation passed. No live loan approval or financial record was changed during verification.
