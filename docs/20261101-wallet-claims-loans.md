# Wallet_best 12.2 — réclamations et crédits

Report adapté des corrections initialement réalisées sur la branche Wallet-final, à la demande de l'utilisateur. Le dépôt cible est `Finuseco/Wallet_best` ; les fonctionnalités USDT, CPK, abonnements, Starlink et autres services existants sont conservées.

- Réclamations : statuts « réservé » / « litigieux », montant net et raison complète de rejet transmis par l'API.
- Crédits : contrat web pour montant restant, devise réelle, échéances, intérêts et pénalités ; conditions et total présentés avant confirmation.
- Les conditions sont également affichées dans la page plein écran des prêts propre à Wallet_best.
- Journalisation HTTP financière désactivée. Les notifications WebSocket techniques ne deviennent pas de fausses notifications.

Les requêtes et remboursements utilisent les routes backend existantes. Aucun ajout SQL propre au client Android, aucune migration automatique, aucune modification de production et aucune fusion sur main.

Le backend doit disposer du schéma décrit dans `cashpay_web/migrations/20261101_loan_runtime_schema.sql`. Cette migration est fournie séparément pour exécution volontaire via Adminer ; Android ne modifie jamais le schéma.

Validation du report : assemblages debug et release non signé réussis, lint vital release réussi ; contrôles JVM des adaptateurs Moshi compilés et du décodeur de cinq formats de codes (normal, tourné, inversé) réussis. Les scénarios backend PostgreSQL de devis/paiement/remboursement sont également vérifiés. Aucun essai sur téléphone physique ou paiement de production effectué.
