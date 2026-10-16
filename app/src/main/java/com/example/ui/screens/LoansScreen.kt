package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.ui.draw.clip
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.MulishFontFamily
import com.example.ui.theme.ToofanMainDark
import com.example.ui.viewmodel.DashboardUiState
import com.example.ui.viewmodel.DashboardViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoansScreen(
    uiState: DashboardUiState,
    viewModel: DashboardViewModel,
    onBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Prêts CashPay", fontFamily = MulishFontFamily, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Retour")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = ToofanMainDark,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color(0xFFF8F9FD))
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Re-using the sub-tab logic from LoansDialog
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White, androidx.compose.foundation.shape.RoundedCornerShape(10.dp))
                    .padding(4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                val tabs = listOf("Offres", "Prêt Actif", "Historique")
                tabs.forEachIndexed { index, label ->
                    val isSelected = uiState.loanSelectedTab == index
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .height(34.dp)
                            .clip(androidx.compose.foundation.shape.RoundedCornerShape(8.dp))
                            .clickable { viewModel.setLoanSelectedTab(index) },
                        color = if (isSelected) Color(0xFF059669) else Color.Transparent
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = label,
                                fontFamily = MulishFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = if (isSelected) Color.White else Color(0xFF0F172A)
                            )
                        }
                    }
                }
            }
            
            // Content based on tab
            when (uiState.loanSelectedTab) {
                0 -> SubTabLoanOffer(uiState, viewModel)
                1 -> SubTabActiveLoan(uiState, viewModel)
                2 -> SubTabLoanHistory(uiState, viewModel)
            }
        }
    }
}
