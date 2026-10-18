package com.example.ui.theme

import androidx.compose.ui.graphics.Color

// Official Brand Colors identified from user's card design
val BrandBlue = Color(0xFF0C2A87)         // Deep Royal Blue from user card
val BrandBlueVibrant = Color(0xFF004BE5)  // Radiant Electric Blue
val BrandBlueDark = Color(0xFF061548)     // Deep Blue Navy (replacing old 000E38)
val BrandBlueCyan = Color(0xFF00E5FF)     // Neon Cyan Wave Glow

// Official Brand Orange requested by user: #FF6600
val BrandOrange = Color(0xFFFF6600)       // Exact FF6600 requested by user
val BrandOrangeDark = Color(0xFFE65100)   // Deep warm orange for gradient
val BrandOrangeLight = Color(0xFFFF8533)  // Bright orange highlight

// Aliases for compatibility
val PrimaryBlue = BrandBlue
val PrimaryBlueDark = BrandBlueDark
val PrimaryBlueLight = BrandBlueVibrant

// App Design System Tokens
val ToofanMainDark = BrandBlueDark
val ToofanBodyText = Color(0xFF64748B)
val ToofanBgColor = Color(0xFFF6F8FC)
val ToofanWhite = Color(0xFFFFFFFF)
val ToofanGrey1 = Color(0xFFE2E8F0)
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

val CancelGrey = Color(0xFF64748B)        // Gris neutre pour Annulation, Retour
val CancelBg = Color(0xFFF1F5F9)

// Service Card Colors
val ToofanOrange = BrandOrange
val ToofanBlue = BrandBlueVibrant
val ToofanYellow = WarningYellow

// Secondary & Dark Mode Utility
val DarkBackground = Color(0xFF05102E)
val DarkSurface = Color(0xFF0A1C4D)
val DarkSurfaceVariant = Color(0xFF132B6E)
val DarkTextPrimary = Color(0xFFFFFFFF)
val DarkTextSecondary = Color(0xFFA5A7C4)
