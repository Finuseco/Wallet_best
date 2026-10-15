package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.MulishFontFamily
import com.example.ui.theme.ToofanGreen
import com.example.ui.theme.ToofanGrey1
import com.example.ui.theme.ToofanMainDark
import com.example.ui.theme.ToofanWhite

@Composable
fun OtpInputField(
    otpValue: String,
    onOtpChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    otpLength: Int = 6,
    isError: Boolean = false
) {
    BasicTextField(
        value = otpValue,
        onValueChange = { input ->
            val digits = input.filter { it.isDigit() }.take(otpLength)
            onOtpChange(digits)
        },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
        modifier = modifier.testTag("otp_input_field"),
        decorationBox = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                for (i in 0 until otpLength) {
                    val char = otpValue.getOrNull(i)?.toString() ?: ""
                    val isFocused = otpValue.length == i

                    val borderColor by animateColorAsState(
                        targetValue = when {
                            isError -> Color(0xFFFF4868)
                            isFocused -> ToofanGreen
                            char.isNotEmpty() -> ToofanGreen
                            else -> ToofanGrey1
                        },
                        label = "toofan_otp_border"
                    )

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(52.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(ToofanWhite)
                            .border(
                                width = if (isFocused || char.isNotEmpty()) 1.8.dp else 1.dp,
                                color = borderColor,
                                shape = RoundedCornerShape(10.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = char,
                            fontSize = 22.sp,
                            fontFamily = MulishFontFamily,
                            fontWeight = FontWeight.Bold,
                            color = ToofanMainDark,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    )
}
