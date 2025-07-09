package com.lmar.checkersgame.presentation.common.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage

@Composable
fun ImageCircle(
    imageUrl: String,
    modifier: Modifier = Modifier,
    contentDescription: String = "Circular Image"
) {
    AsyncImage(
        model = imageUrl,
        contentDescription = contentDescription,
        modifier = modifier
            .size(100.dp) // Ajusta el tamaño de la imagen
            .clip(CircleShape) // Hace que la imagen sea circular
            .border(5.dp, MaterialTheme.colorScheme.tertiary, CircleShape), // Borde opcional
        contentScale = ContentScale.Crop // Ajusta la imagen para que llene el círculo
    )
}

@Composable
fun ImageCircle(
    painter: Painter,
    modifier: Modifier = Modifier,
    contentDescription: String = "Circular Image"
) {
    Image(
        painter = painter,
        contentDescription = contentDescription,
        modifier = modifier
            .size(100.dp) // Ajusta el tamaño de la imagen
            .clip(CircleShape) // Hace que la imagen sea circular
            .border(5.dp, MaterialTheme.colorScheme.tertiary, CircleShape), // Borde opcional
        contentScale = ContentScale.Crop // Ajusta la imagen para que llene el círculo
    )
}