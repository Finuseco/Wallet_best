# Crédits, répertoire et Jojo Shopping

Le bouton Crédit de l’accueil ouvre l’écran des offres, prêt actif et historique. L’ancien dialogue est désormais rendu au niveau principal, et non exclusivement dans l’onglet Cartes. Le bouton Retour Android quitte cet écran. Les appels de crédits restaurent la session avant la requête ; les devis, conditions acceptées et écritures restent validés par Cashpay Web.

Les contacts utilisent `POST /api/v1/contacts/resolve` en lots de 250, sans abandonner le reste du répertoire après 200 contacts. La liste reste visible pendant la détection. Les correspondances complètes utilisent l’indicatif et le pays, jamais seulement neuf derniers chiffres. La déduplication utilise l’ID Wallet pour les comptes détectés. Les lignes affichent nom du répertoire, nom du compte, ID Wallet, téléphone et avatar avec largeur limitée. Actualiser relance la détection ; l’invitation ouvre le partage système et ne prétend plus avoir envoyé une invitation sans action.

La liste des boutiques provient de la route privée authentifiée, sans repli silencieux vers une route absente ou un profil public. Une panne ne devient pas une liste vide réussie. Le chargement et l’erreur boutiques sont distincts de ceux des produits ; une erreur propose Réessayer plutôt que Créer ma première boutique. Une lecture réussie remplace les anciennes boutiques au lieu de conserver les boutiques supprimées.

Déployer d’abord le correctif backend `cashpay_web` documenté dans `docs/20261106-wallet-best-credit-contacts-shopping.md`, puis installer la nouvelle version Android. Aucun changement SQL nécessaire pour ces corrections.

Vérification : compilation `:app:compileDebugKotlin` avec SDK 36.1 ; contrats backend exécutés sur une base PostgreSQL temporaire, sans fournisseur ni base de production. Les scénarios de vérification restent hors du dépôt. Aucun nouvel APK signé de production ni test sur téléphone physique n’est annoncé par cette livraison. Wallet Final n’a pas été modifié.
