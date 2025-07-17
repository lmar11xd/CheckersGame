package com.lmar.checkersgame.presentation.ui.components.game

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lmar.checkersgame.presentation.ui.components.formatTime

@Composable
fun GameHeaderInfo(
    gameTime: Int,
    title: String,
    label: String,
    isUserTurn: Boolean,
    opponentName: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            modifier = Modifier.width(120.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                modifier = Modifier.size(18.dp).padding(end = 4.dp),
                imageVector = Icons.Default.AccessTime,
                contentDescription = "Tiempo",
                tint = MaterialTheme.colorScheme.onPrimary
            )
            Text(formatTime(gameTime), color = MaterialTheme.colorScheme.onPrimary, fontSize = 12.sp)
        }

        Box(
            modifier = Modifier
                .padding(horizontal = 4.dp)
                .background(MaterialTheme.colorScheme.onPrimary, shape = CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Row(modifier = Modifier.padding(horizontal = 8.dp)) {
                Text(title, color = Color.DarkGray, fontSize = 12.sp)
                Spacer(modifier = Modifier.width(4.dp))
                Text(label, color = Color.DarkGray, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }

        Row(
            modifier = Modifier.width(120.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = if (isUserTurn) "Tu turno" else "Turno de $opponentName",
                fontSize = 12.sp,
                textAlign = TextAlign.End,
                color = MaterialTheme.colorScheme.onPrimary
            )
        }
    }
}