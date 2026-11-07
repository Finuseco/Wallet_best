# Crédits, session et répertoire : correction complémentaire

Le clic **Crédit & Prêts** ouvre maintenant une destination exclusive avant la composition du tableau de bord. Les autres surfaces ne peuvent plus concurrencer son affichage ; le retour Android et le bouton Retour restaurent l’accueil. Les offres, le prêt courant, l’historique et les confirmations restent branchés sur les routes existantes, sans modification des calculs.

La session HTTP ne prend plus un cookie `next-auth.session-token` ou un autre cookie contenant « session » pour le jeton CashPay. Seuls les noms exacts CashPay sont restaurés, avec un filtre de format JWT ; la signature et les droits restent vérifiés par le backend. Les cookies sont limités à l’origine HTTPS CashPay. Les identifiants et cookies sont tous effacés à la déconnexion, et le compte local est synchronisé avant les appels privés. Une réponse boutiques portant une identité différente est refusée et demande une reconnexion. Un succès HTTP avec `success:false` n’est plus traité comme un catalogue valide.

La lecture des boutiques est prioritaire sur l’invitation à créer une première boutique. La création supplémentaire reste disponible après lecture réussie ; le titre et son bouton sont empilés pour éviter les collisions sur téléphone. Les boutiques viennent exclusivement de l’API privée du compte connecté.

Le répertoire relance sa détection à chaque ouverture autorisée. Les lignes affichent le nom du téléphone, le nom public et l’ID Wallet sans séparateur vide, le numéro et l’avatar. Les initiales prénom/nom restent derrière l’image pour couvrir une image absente ou en échec. Le libellé « Identifiés » ne prétend pas mesurer la présence en ligne. Les contacts reconnus et les autres restent séparés.

Installer d’abord le backend CashPay Web décrit dans `docs/20261106-wallet-best-navigation-session.md`, puis construire et distribuer une nouvelle version Android. Un `git pull` côté serveur ne met pas à jour l’application déjà installée sur le téléphone. Si la session était déjà corrompue, se déconnecter puis se reconnecter dans la nouvelle version. Aucun SQL nécessaire.

Validation : compilation Kotlin Android et contrats SQL/API isolés. Vérification JVM du véritable ApiClient compilé : cookies exacts, exclusion des sessions étrangères, restauration et nettoyage de déconnexion. Aucun test, faux contact ou prix simulé ajouté au code livré. Pas de validation sur téléphone physique ni d’APK de production signé dans cette session.
