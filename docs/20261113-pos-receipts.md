# Bordereaux de transaction POS — Wallet Best 12.4

L’historique ouvre un ticket thermique 58 ou 80 mm, avec le logo `cashpay_logo` déjà utilisé par l’application. Le même moteur dessine l’aperçu, le PDF partagé/téléchargé et le document imprimé : aucune fiche A4 différente et aucun emblème « CP » fabriqué.

Le bordereau reprend le type d’opération du catalogue existant, le statut, la référence, la date, les parties et wallets disponibles, le libellé, le montant et les frais. Une entrée n’ajoute pas les frais de l’émetteur à son crédit. Les devises fiat respectent leur précision ISO ; BTC conserve huit décimales et USDT/USDC six. Les noms et références longs se répartissent sur plusieurs lignes.

Le QR code et le CODE128 contiennent uniquement la référence : ni demande de paiement, ni lien de vérification inventé. Un code trop long pour être imprimé intégralement est omis, jamais recadré. La référence reste lisible en texte. Les statuts en cours, échoués ou inconnus ne produisent pas une confirmation de paiement. La mention de génération électronique n’est pas une signature cryptographique.

Coordonnées de pied de page reprises du site public CashPay : info@cashpay-all.com, +243 85 386 80 21, www.cashpay-all.com.

Télécharger ouvre le sélecteur de destination Android, sans permission de stockage générale. Partager fournit le PDF au moyen de FileProvider et d’une autorisation de lecture temporaire. Imprimer ouvre le service d’impression Android avec une taille POS personnalisée ; les marges et formats choisis dans le service sont pris en compte, avec pagination si nécessaire. Il faut un service compatible avec l’imprimante (y compris les connexions USB/Bluetooth proposées par ce service).

Une seconde sortie directe réseau ESC/POS permet de saisir l’adresse IP et le port de l’imprimante, par défaut 9100. Elle envoie un raster monochrome standard de 384 ou 576 points en blocs de 128 lignes, sans commande propriétaire ni massicot imposé. Aucun accès Bluetooth ni permission de stockage supplémentaire n’est demandé. Une limite de connexion et un délai de fermeture évitent un envoi bloqué. Un envoi TCP n’est pas une confirmation matérielle : les erreurs avertissent du risque de doublon et demandent de vérifier le papier avant de réessayer. L’impression silencieuse Sunmi/Pax/ESC-POS nécessite un modèle, sa connexion et son SDK : elle n’est pas présentée comme déjà testée sur un terminal physique.

## Vérification

Compilation Android debug réussie. Tests JVM exécutant les méthodes réelles de montant/statut : décimales BTC, stablecoins, JPY, arrondi EUR, frais entrants/sortants et statuts non confirmés. Test de l’encodeur ESC/POS réel : décodage des blocs 58/80 mm, exactitude des pixels, plusieurs blocs de 128 lignes, fond transparent blanc et avance papier sans massicot. Les tests temporaires et clés de signature debug restent hors du dépôt. Le rendu PDF sur appareil, la remise au sélecteur de fichiers et l’impression sur un matériel physique restent à vérifier en recette.
