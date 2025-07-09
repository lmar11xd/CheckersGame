package com.lmar.checkersgame.presentation.common.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Checkbox
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lmar.checkersgame.core.ui.theme.Shapes

@Composable
fun NormalTextComponent(
    value: String,
    modifier: Modifier = Modifier,
    fontSize: TextUnit = 14.sp,
    textColor: Color = MaterialTheme.colorScheme.tertiary,
    textAlign: TextAlign = TextAlign.Center
) {
    Text(
        text = value,
        modifier = modifier.fillMaxWidth().heightIn(),
        style = TextStyle(
            fontSize = fontSize,
            fontWeight = FontWeight.Normal,
            fontStyle = FontStyle.Normal,
            textAlign = textAlign
        ),
        color = textColor
    )
}

@Composable
fun HeadingTextComponent(
    value: String,
    modifier: Modifier = Modifier,
    fontSize: TextUnit = 18.sp,
    textColor: Color = MaterialTheme.colorScheme.primary,
    textAlign: TextAlign = TextAlign.Center
) {
    Text(
        text = value,
        modifier = modifier.fillMaxWidth().heightIn(),
        style = TextStyle(
            fontSize = fontSize,
            fontWeight = FontWeight.Bold,
            fontStyle = FontStyle.Normal,
            textAlign = textAlign
        ),
        color = textColor
    )
}

@Composable
fun FormTextField(
    modifier: Modifier = Modifier,
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    icon: ImageVector
) {
    OutlinedTextField(
        value = value,
        modifier = modifier
            .fillMaxWidth()
            .clip(Shapes.small)
            .padding(start = 4.dp, end = 4.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            focusedLabelColor = MaterialTheme.colorScheme.primary,
            cursorColor = MaterialTheme.colorScheme.primary,
            focusedTextColor = MaterialTheme.colorScheme.primary,
            focusedLeadingIconColor = MaterialTheme.colorScheme.primary,
            focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        label = { Text(label) },
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
        onValueChange = onValueChange,
        leadingIcon = {
            Icon(imageVector = icon, contentDescription = "IconForm")
        },
        singleLine = true,
        maxLines = 1
    )
}

@Composable
fun FormPasswordTextField(
    modifier: Modifier = Modifier,
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    icon: ImageVector
) {
    val localFocusManager = LocalFocusManager.current

    val passVisible = remember {
        mutableStateOf(false)
    }

    OutlinedTextField(
        value = value,
        modifier = modifier
            .fillMaxWidth()
            .clip(Shapes.small)
            .padding(start = 4.dp, end = 4.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            focusedLabelColor = MaterialTheme.colorScheme.primary,
            cursorColor = MaterialTheme.colorScheme.primary,
            focusedTextColor = MaterialTheme.colorScheme.primary,
            focusedLeadingIconColor = MaterialTheme.colorScheme.primary,
            focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        label = { Text(label) },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done),
        keyboardActions = KeyboardActions {
            localFocusManager.clearFocus()
        },
        onValueChange = onValueChange,
        leadingIcon = {
            Icon(imageVector = icon, contentDescription = "IconForm")
        },
        trailingIcon = {
            val iconImage = if(passVisible.value) {
                Icons.Filled.Visibility
            } else {
                Icons.Filled.VisibilityOff
            }

            val description = if(passVisible.value) {
                "Ocultar Contraseña"
            } else {
                "Mostrar Contraseña"
            }

            IconButton(onClick = { passVisible.value = !passVisible.value }) {
                Icon(imageVector = iconImage, contentDescription = description)
            }
        },
        visualTransformation = if(passVisible.value) VisualTransformation.None else PasswordVisualTransformation(),
        singleLine = true,
        maxLines = 1
    )
}

@Composable
fun FormCheckbox(value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(56.dp)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        val checkedState = remember {
            mutableStateOf(false)
        }

        Checkbox(
            checked = checkedState.value,
            onCheckedChange = { checkedState.value = !checkedState.value }
        )

        NormalTextComponent(value, fontSize = 16.sp, textAlign = TextAlign.Start)
    }
}

@Composable
fun DividerTextComponent() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        HorizontalDivider(
            modifier = Modifier.fillMaxWidth().weight(1f),
            color = Color.Gray,
            thickness = 1.dp
        )
        Text("o", modifier = Modifier.padding(8.dp), fontSize = 18.sp, color = Color.Gray)
        HorizontalDivider(
            modifier = Modifier.fillMaxWidth().weight(1f),
            color = Color.Gray,
            thickness = 1.dp
        )
    }
}