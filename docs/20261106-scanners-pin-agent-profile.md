# Scanners et paramètres du compte

Les résultats caméra, image et saisie du scanner passent par Cashpay Web avant utilisation. Le QR portefeuille sert à identifier un utilisateur/transférer ; les articles rejoignent le panier depuis le catalogue ; la demande de montant fixe ouvre son devis serveur, avec frais distincts des achats POS sans frais. Un produit ne peut pas être utilisé comme identité d’un client agent. Aucun prix manuel ou taux de secours USD/CDF/EUR n’est créé. Le devis final signé est affiché avant PIN.

Le retrait dans l’espace Agent emploie agent_withdraw et conserve les jetons d’identification, montant, confirmation et OTP. Le remboursement utilise la route existante loan-repayment, charge le prêt dans sa devise et affiche frais/débit avant PIN. Les erreurs ne sont plus remplacées par un prêt supposé depuis un profil public. Aucun montant de commission ou référence fictive n’est ajouté.

Les devis fictifs Mobile Money (solde de secours de 1 000 et pourcentages locaux), les soldes fictifs de dépôt agent et le succès simulé de bascule de commission après erreur ont été retirés. Une panne conserve le dernier solde connu, affiche une erreur et ne fait pas avancer vers une validation financière.

Un écran dédié permet de changer le PIN du portefeuille en renseignant ancien PIN, nouveau PIN et confirmation. Le PIN bancaire d’une carte conserve son parcours séparé. Le profil permet de modifier téléphone avec indicatif, nom, ville, profession et adresse, avec PIN masqué/validation serveur. Le corps utilise les colonnes attendues par l’API.

Devenir Agent charge et affiche le tarif réel du plan avant confirmation, puis transmet acceptedFee. L’activation reste indisponible si le tarif n’a pas pu être chargé. Un changement de formule efface le tarif et le PIN pour une nouvelle confirmation.

Déployer d’abord le backend décrit dans cashpay_web/docs/20261106-wallet-scanners-settings.md, puis installer cette version Android. Aucun SQL supplémentaire. Compilation Kotlin et essais de contrat/SQL réalisés hors production ; vérification tactile et caméra sur téléphone réel encore nécessaire. Aucun scénario de test, donnée de démonstration ou nouvel APK de production signé n’est livré. Wallet Final n’a pas été modifié.
