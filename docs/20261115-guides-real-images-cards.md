# CashPay guide images and empty card accounts

The four-page Android introduction now uses only the two supplied CashPay images: three cropped panels for access, PIN security and the agent workspace, then the portrait photo with a compact services overview. Image backgrounds fill the window with crop scaling; controls respect system insets. The landscape source is low resolution, so its crops retain that limitation. No generated replacement images were added. Obsolete generated guide assets were removed. The v5 introduction preference presents this guide once after updating.

The empty card list previously constructed an active Visa preview ending in 4820. That synthetic card has been removed. Empty accounts show an explicit empty state and the ordering action. Card refresh clears the previous list; failures show a retry action instead of retaining earlier results.

Inspected CashPay Web routes `/api/v1/cards` and `/api/v1/wallet`: both obtain cards via `getCardsForUser` with the authenticated user ID. The query filters `cards.user_id = $1`; it does not insert synthetic cards. No backend or production database changes were needed for the removed Android preview.
