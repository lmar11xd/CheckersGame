package com.lmar.checkersgame.presentation.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val AppTextInputIconSize = 12.dp

private val AppTextInputColors: TextFieldColors
    @Composable
    get() = OutlinedTextFieldDefaults.colors(
        focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
        unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
        cursorColor = MaterialTheme.colorScheme.tertiary,
        focusedLabelColor = MaterialTheme.colorScheme.tertiary,
        unfocusedLabelColor = MaterialTheme.colorScheme.outline,
        focusedBorderColor = MaterialTheme.colorScheme.tertiary,
        unfocusedBorderColor = MaterialTheme.colorScheme.outline,
        focusedLeadingIconColor = MaterialTheme.colorScheme.outline,
        unfocusedLeadingIconColor = MaterialTheme.colorScheme.outline,
        focusedTrailingIconColor = MaterialTheme.colorScheme.outline,
        unfocusedTrailingIconColor = MaterialTheme.colorScheme.outline,
        errorBorderColor = MaterialTheme.colorScheme.outline,
        errorTextColor = MaterialTheme.colorScheme.outline,
        errorLeadingIconColor = MaterialTheme.colorScheme.outline,
        errorTrailingIconColor = MaterialTheme.colorScheme.outline,
        errorLabelColor = MaterialTheme.colorScheme.outline,
        errorSupportingTextColor = MaterialTheme.colorScheme.error,
        focusedSupportingTextColor = MaterialTheme.colorScheme.outline,
        unfocusedSupportingTextColor = MaterialTheme.colorScheme.outline,
        focusedTextColor = MaterialTheme.colorScheme.tertiary,
        unfocusedTextColor = MaterialTheme.colorScheme.outline
    )

@Composable
fun RoomTextField(
    modifier: Modifier = Modifier,
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    enabled: Boolean = true,
    keyboardType: KeyboardType = KeyboardType.Text,
    // icons param
    leadingIcon: Painter? = null,
    onTrailingIconClick: () -> Unit = {},
    trailingIcon: Painter? = null,
) {
    OutlinedTextField(
        modifier = modifier
            .padding(start = 4.dp, end = 4.dp),
        colors = AppTextInputColors,
        value = value,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        textStyle = TextStyle(fontSize = 12.sp),
        onValueChange = onValueChange,
        enabled = enabled,
        label = {
            Text(text = label, fontSize = 12.sp)
        },
        // icons
        leadingIcon = leadingIcon?.let {
            @Composable {
                Icon(
                    modifier = Modifier.size(AppTextInputIconSize),
                    painter = leadingIcon,
                    contentDescription = null
                )
            }
        },
        trailingIcon = trailingIcon?.let {
            @Composable {
                IconButton(onClick = onTrailingIconClick) {
                    Icon(
                        modifier = Modifier.size(AppTextInputIconSize),
                        painter = trailingIcon,
                        contentDescription = null
                    )
                }
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
fun RoomTextFieldFieldPreview() {
    RoomTextField(value = "Valor del campo", label = "Nombre del campo", onValueChange = {})
}