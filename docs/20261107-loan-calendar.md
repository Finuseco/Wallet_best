# Calendrier des prêts et grâce de recouvrement

Les dates de début, fin, échéances et paiements affichent désormais le jour de la semaine et la date complète en français. Exemple : `mardi 10 novembre 2026`. La date contractuelle est conservée lors du changement de fuseau du téléphone ; aucune échéance n’est déplacée. Les formats inconnus restent visibles tels quels plutôt que d’inventer une date.

L’écran de confirmation précise que le recouvrement automatique et les pénalités commencent après sept jours de grâce, et que les anciennes dettes restent dues après la fin du contrat. Les échéances hebdomadaires restent celles calculées par le backend depuis la date d’approbation.

Le moteur de recouvrement est installé dans CashPay Web, pas dans le téléphone : voir [le guide backend](https://github.com/Finuseco/cashpay_web/blob/codex/super-agent-operations/docs/20261107-loan-auto-recovery.md). Il faut exécuter sa migration et démarrer son processus PM2 pour traiter les dépôts sans ouvrir l’application. Reconstruire/distribuer Wallet Best pour obtenir les nouveaux libellés de calendrier.

Vérification : `:app:compileDebugKotlin` réussie ; tests JVM du véritable utilitaire compilé, mardi/dimanche, dates invalides et fuseaux UTC/Kinshasa/Los Angeles. Aucun nouveau paiement, prix ou donnée de démonstration dans le code livré. Aucun APK signé ou essai sur téléphone physique annoncé.
