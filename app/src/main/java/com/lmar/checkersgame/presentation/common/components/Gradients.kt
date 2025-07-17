package com.lmar.checkersgame.presentation.common.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.lmar.checkersgame.R
import com.lmar.checkersgame.core.ui.theme.CheckersGameTheme

@Composable
fun GradientButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(64.dp),
        border = BorderStroke(
            width = 4.dp,
            brush = Brush.linearGradient(
                listOf(colorResource(R.color.pink_light), colorResource(R.color.green_light))
            )
        ),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Transparent,
            contentColor = Color.White
        )
    ) {
        Text(text)
    }
}

@Composable
fun GradientCircleImage(
    image: Painter,
    modifier: Modifier = Modifier,
    imageSize: Dp = 100.dp,
    strokeWidth: Dp = 4.dp,
    zoom: Float = 1.15f
) {
    val borderSize = imageSize + strokeWidth * 2

    Box(
        modifier = modifier
            .size(borderSize)
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(
                        colorResource(R.color.pink_light),
                        colorResource(R.color.green_light)
                    )
                ),
                shape = CircleShape
            ),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = image,
            contentDescription = "Avatar",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(imageSize)
                .clip(CircleShape)
                .graphicsLayer(
                    scaleX = zoom,
                    scaleY = zoom
                )
                .border(
                    width = 0.dp,
                    color = Color.Transparent,
                    shape = CircleShape
                )
        )
    }
}

@Composable
fun GradientCard(
    modifier: Modifier = Modifier,
    cardRadius: Dp = 8.dp,
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier
            .border(
                width = 2.dp,
                brush = Brush.linearGradient(
                    colors = listOf(
                        colorResource(R.color.pink_light),
                        colorResource(R.color.green_light)
                    )
                ),
                shape = RoundedCornerShape(cardRadius)
            ),

        ) {
        content()
    }
}

@Preview(showBackground = true)
@Composable
private fun GradientsPreview() {
    CheckersGameTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(color = Color.Black)
                .padding(16.dp)
        ) {
            GradientCircleImage(
                image = painterResource(id = R.drawable.default_avatar),
                modifier = Modifier.align(Alignment.CenterHorizontally),
                imageSize = 96.dp,
                strokeWidth = 6.dp
            )

            Spacer(modifier = Modifier.size(16.dp))

            GradientCard(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(10.dp)
                ) {
                    Text("1.", color = Color.White)
                    Text("Card View", color = Color.White)
                }
            }

            Spacer(modifier = Modifier.size(16.dp))

            GradientButton("Login", {}, modifier = Modifier.fillMaxWidth())
        }
    }
}