package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.MulishFontFamily
import com.example.ui.theme.ToofanBodyText
import com.example.ui.theme.ToofanGreen
import com.example.ui.theme.ToofanGrey1
import com.example.ui.theme.ToofanMainDark
import com.example.ui.theme.ToofanWhite

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.input.VisualTransformation

@Composable
fun ToofanButton(
    title: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    isLoading: Boolean = false,
    containerColor: Color? = null,
    contentColor: Color = ToofanWhite,
    testTag: String = "toofan_button"
) {
    // Premium brand gradient from the screenshots (Violet/Purple to Cyan/Blue)
    val defaultGradient = Brush.linearGradient(listOf(Color(0xFF8B5CF6), Color(0xFF06B6D4)))
    
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp) // Agrandissement à 56dp pour de meilleures cibles de touche
            .clip(RoundedCornerShape(14.dp))
            .clickable(enabled = enabled && !isLoading, onClick = onClick)
            .testTag(testTag),
        shape = RoundedCornerShape(14.dp),
        color = Color.Transparent, // Clear to let background brush show
        shadowElevation = if (enabled) 2.dp else 0.dp
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = if (containerColor != null) {
                        Brush.linearGradient(listOf(containerColor, containerColor))
                    } else {
                        defaultGradient
                    },
                    alpha = if (enabled) 1f else 0.5f
                )
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = contentColor,
                    strokeWidth = 2.5.dp
                )
            } else {
                Text(
                    text = title,
                    color = contentColor,
                    fontFamily = MulishFontFamily,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 16.sp
                )
            }
        }
    }
}

@Composable
fun ToofanInputField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "",
    leadingIcon: (@Composable () -> Unit)? = null,
    trailingIcon: (@Composable () -> Unit)? = null,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    singleLine: Boolean = true,
    testTag: String = "toofan_input_field"
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(ToofanWhite)
            .border(1.dp, ToofanGrey1.copy(alpha = 0.8f), RoundedCornerShape(14.dp))
            .padding(horizontal = 14.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (leadingIcon != null) {
                leadingIcon()
            }

            Box(modifier = Modifier.weight(1f)) {
                if (value.isEmpty()) {
                    Text(
                        text = placeholder,
                        color = ToofanBodyText.copy(alpha = 0.5f),
                        fontFamily = MulishFontFamily,
                        fontSize = 15.sp
                    )
                }
                BasicTextField(
                    value = value,
                    onValueChange = onValueChange,
                    singleLine = singleLine,
                    visualTransformation = visualTransformation,
                    textStyle = TextStyle(
                        color = ToofanMainDark,
                        fontSize = 15.sp,
                        fontFamily = MulishFontFamily,
                        fontWeight = FontWeight.Medium
                    ),
                    keyboardOptions = keyboardOptions,
                    keyboardActions = keyboardActions,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag(testTag)
                )
            }

            if (trailingIcon != null) {
                trailingIcon()
            }
        }
    }
}
