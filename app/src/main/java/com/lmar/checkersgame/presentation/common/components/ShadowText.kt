package com.lmar.checkersgame.presentation.common.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ShadowText(
    modifier: Modifier = Modifier,
    text: String,
    fontSize: TextUnit = 24.sp,
    fontWeight: FontWeight = FontWeight.Bold,
    fontFamily: FontFamily = FontFamily.Default,
    textColor: Color = Color.White,
    textAlign: TextAlign = TextAlign.Center,
    shadowColor: Color = Color.Black,
    blurRadius: Float = 20f,
    xOffset: Float = 0f,
    yOffset: Float = 0f
) {
    Text(
        text = text,
        textAlign = textAlign,
        style = TextStyle(
            fontSize = fontSize,
            fontFamily = fontFamily,
            fontWeight = fontWeight,
            color = textColor,
            shadow = Shadow(
                color = shadowColor, // Color de la sombra
                offset = Offset(xOffset, yOffset), // Desplazamiento en X e Y
                blurRadius = blurRadius // Nivel de desenfoque
            )
        ),
        modifier = modifier.padding(5.dp)
    )
}

@Preview(showBackground = true)
@Composable
fun PreviewShadowText() {
    Column {
        ShadowText(
            text = "Hola Mundo",
            shadowColor = Color.Blue,
            blurRadius = 20f
        )

        ShadowText(
            text = "Hola Mundo", fontSize = 48.sp,
            textColor = Color.Green.copy(alpha = 0.5f),
            shadowColor = Color.Green,
            blurRadius = 20f
        )

        ShadowText(
            text = "X", fontSize = 48.sp,
            textColor = Color.Blue.copy(alpha = 0.5f),
            shadowColor = Color.Blue,
            blurRadius = 20f
        )
    }
}