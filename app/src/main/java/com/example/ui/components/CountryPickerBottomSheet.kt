package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CountryDto
import com.example.ui.theme.MulishFontFamily
import com.example.ui.theme.ToofanBodyText
import com.example.ui.theme.ToofanGreen
import com.example.ui.theme.ToofanGrey1
import com.example.ui.theme.ToofanMainDark
import com.example.ui.theme.ToofanWhite

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CountryPickerBottomSheet(
    sheetState: SheetState,
    countries: List<CountryDto>,
    selectedCountry: CountryDto,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onCountrySelected: (CountryDto) -> Unit,
    onDismissRequest: () -> Unit
) {
    val filteredCountries = remember(countries, searchQuery) {
        if (searchQuery.isBlank()) {
            countries
        } else {
            val q = searchQuery.trim().lowercase()
            countries.filter {
                it.name.lowercase().contains(q) ||
                        it.code.lowercase().contains(q) ||
                        it.dialCode.contains(q)
            }
        }
    }

    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = sheetState,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        containerColor = ToofanWhite,
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(vertical = 12.dp)
                    .width(40.dp)
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(ToofanGrey1)
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.75f)
                .padding(horizontal = 20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Sélectionnez votre pays",
                    fontSize = 20.sp,
                    fontFamily = MulishFontFamily,
                    fontWeight = FontWeight.Bold,
                    color = ToofanMainDark
                )
                IconButton(
                    onClick = onDismissRequest,
                    modifier = Modifier.testTag("close_country_picker")
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Fermer",
                        tint = ToofanBodyText
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = onSearchQueryChange,
                placeholder = { Text("Rechercher un pays, code ou indicatif...", fontFamily = MulishFontFamily) },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Rechercher",
                        tint = ToofanGreen
                    )
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { onSearchQueryChange("") }) {
                            Icon(Icons.Default.Close, contentDescription = "Effacer")
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(10.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = ToofanGreen,
                    unfocusedBorderColor = ToofanGrey1,
                    focusedContainerColor = ToofanWhite,
                    unfocusedContainerColor = ToofanWhite
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("country_search_input")
            )

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = "${filteredCountries.size} pays configurés",
                fontFamily = MulishFontFamily,
                fontSize = 13.sp,
                color = ToofanBodyText
            )

            Spacer(modifier = Modifier.height(8.dp))

            LazyColumn(
                modifier = Modifier.fillMaxWidth()
            ) {
                items(filteredCountries, key = { it.code }) { country ->
                    val isSelected = country.code == selectedCountry.code
                    CountryItemRow(
                        country = country,
                        isSelected = isSelected,
                        onClick = { onCountrySelected(country) }
                    )
                    HorizontalDivider(
                        color = ToofanGrey1.copy(alpha = 0.4f),
                        thickness = 0.8.dp
                    )
                }
            }
        }
    }
}

@Composable
private fun CountryItemRow(
    country: CountryDto,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .background(
                if (isSelected) ToofanGreen.copy(alpha = 0.08f) else Color.Transparent
            )
            .padding(horizontal = 8.dp, vertical = 14.dp)
            .testTag("country_item_${country.code}"),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = country.flagEmoji,
            fontSize = 26.sp,
            modifier = Modifier.padding(end = 14.dp)
        )

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = country.name,
                fontFamily = MulishFontFamily,
                fontSize = 15.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                color = ToofanMainDark
            )
            Text(
                text = country.code,
                fontFamily = MulishFontFamily,
                fontSize = 12.sp,
                color = ToofanBodyText
            )
        }

        Text(
            text = country.dialCode,
            fontFamily = MulishFontFamily,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = if (isSelected) ToofanGreen else ToofanBodyText,
            modifier = Modifier.padding(end = 10.dp)
        )

        if (isSelected) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = "Sélectionné",
                tint = ToofanGreen,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}
