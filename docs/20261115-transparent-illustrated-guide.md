# Transparent illustrations integrated into Wallet Best 12.5

Supersedes the schematic-only introduction. The five generated illustrations are now bundled as `drawable-nodpi/guide_illustration_*.png`: welcome, account creation, security, wallet/cards, payments/services. The fifth was generated for this integration. Alpha channels were verified on all five originals; files were copied unchanged from generated_images and no original was deleted.

Each guide page uses ContentScale.Fit in the full-width illustration region: no crop, stretch, photo background or opaque image frame. Black covers the full window behind both system bars; the logo, captions, progress and navigation respect safe insets. Text remains in Compose, outside the illustrations. Services and card pictures are educational artwork, never a user's actual cards or balance. The real card list is unchanged.

Next/Start explicitly use #7A22D4, matching cashpay-main origin/main 3831815, with white text. Back/Next progress through five pages; Start is only on the last. Saved progress is clamped to the page range. The v7 preference replays the new guide once after update. Version code 125 / version name 12.5 identifies the revised Android app.
