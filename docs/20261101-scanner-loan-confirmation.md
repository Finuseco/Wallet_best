# Wallet_best — scanner et paiements vérifiés

- Le scanner conserve les modes et le décodage avancé ML Kit de Wallet_best. Le format du code distingue produit et portefeuille ; les plans caméra respectent le pas des lignes/pixels et les ressources sont libérées à la fermeture.
- Les articles, stocks, vendeurs et prix proviennent du catalogue backend. Le panier conserve les identifiants réels des articles et empêche le mélange de magasins. Aucun prix de secours n'autorise un paiement.
- Les paiements de scans passent par un devis serveur signé : vendeur, quantité, devise, change et frais sont affichés avant saisie du PIN. La réponse serveur fournit le reçu ; un échec ne produit pas de succès local.
- La confirmation de crédit décrit intérêts, total à rembourser, garantie, fréquence, échéances et pénalités. Le remboursement affiche montant, frais et total avant confirmation.

Le backend correspondant est la branche `codex/super-agent-operations` de `Finuseco/cashpay_web`. Les tests temporaires et APK restent hors Git. Un essai réel avec caméra et comptes de test demeure nécessaire.
