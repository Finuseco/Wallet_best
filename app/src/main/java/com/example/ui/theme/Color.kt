package com.example.ui.theme

import androidx.compose.ui.graphics.Color

// Palette verified in Finuseco/cashpay-main origin/main (3831815), css/style.css.
// Legacy token names are retained for existing components.
val BrandBlue = Color(0xFF7A22D4)
val BrandBlueVibrant = Color(0xFF59158D)
val BrandBlueDark = Color(0xFF190A28)
val BrandBlueCyan = Color(0xFFA66BE0)
val BrandOrange = Color(0xFF7A22D4)
val BrandOrangeDark = Color(0xFF59158D)
val BrandOrangeLight = Color(0xFFA66BE0)

// Aliases for compatibility
val PrimaryBlue = BrandBlue
val PrimaryBlueDark = BrandBlueDark
val PrimaryBlueLight = BrandBlueVibrant

// App Design System Tokens
val ToofanMainDark = BrandBlueDark
val ToofanBodyText = Color(0xFF7B42AD)
val ToofanBgColor = Color(0xFFF7F3FB)
val ToofanWhite = Color(0xFFFFFFFF)
val ToofanGrey1 = Color(0xFFE6DCF0)
val ToofanLinkPink = BrandOrange

// Semantic Feedback & Signaling Colors (Système de Signalisation)
val SuccessGreen = Color(0xFF108A43)      // Vert foncé officiel : Succès, Validation, Confirmation
val SuccessGreenDark = Color(0xFF0D6E34)  // Vert très foncé pour contrastes
val GreenOnline = Color(0xFF15803D)       // Vert foncé net pour pastille utilisateur connecté
val ToofanGreen = SuccessGreen            // ToofanGreen est le vert de succès (PAS le bleu !)

val WarningYellow = Color(0xFFF59E0B)     // Jaune / Ambre officiel : Avertissement, En attente, Alerte
val WarningYellowDark = Color(0xFFD97706)

val ErrorRed = Color(0xFFDC2626)          // Rouge officiel : Échec, Erreur, Danger, Rejet
val ErrorRedLight = Color(0xFFEF4444)

val CancelGrey = Color(0xFF7B42AD)        // Gris neutre pour Annulation, Retour
val CancelBg = Color(0xFFF1F5F9)

// Service Card Colors
val ToofanOrange = BrandOrange
val ToofanBlue = BrandBlueVibrant
val ToofanYellow = WarningYellow

// Secondary & Dark Mode Utility
val DarkBackground = Color(0xFF190A28)
val DarkSurface = Color(0xFF2A0E42)
val DarkSurfaceVariant = Color(0xFF59158D)
val DarkTextPrimary = Color(0xFFFFFFFF)
val DarkTextSecondary = Color(0xFFA5A7C4)
