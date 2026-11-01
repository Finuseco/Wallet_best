# Wallet_best 12.3 — suivi des réclamations et crédits

Les statuts de traitement inter sont prioritaires sur un ancien statut « pending » : réservé et litige restent en cours ; payé et rejeté sont classés dans l'historique. Le détail conserve la description complète de rejet, affiche son chargement et ses erreurs et propose de réessayer. Les réponses tardives d'un autre dossier ou compte sont ignorées.

La liste vérifie la réponse de succès du serveur avant mise à jour. La page crédits permet d'actualiser offre, prêt courant, échéancier et remboursements ; les montants et devises du contrat backend restent conservés. Les soumissions refusent aussi les montants non finis.

Validation : assemblages debug/release et lint vital release réussis ; contrôles JVM des véritables classes et adaptateurs Moshi pour les statuts/onglets/consignes et les montants, échéances et remboursements de crédits. Aucun test temporaire, APK ou secret dans Git. Les parcours sur téléphone et les paiements de production ne sont pas déclarés testés. Release non signée, à intégrer à la signature habituelle.

Backend associé : PR Finuseco/cashpay_web#3. Aucun nouveau schéma SQL pour cette correction ; les prérequis inter existants restent à appliquer volontairement si absents. Aucune modification OnChat ou Wallet-final dans cette phase.
