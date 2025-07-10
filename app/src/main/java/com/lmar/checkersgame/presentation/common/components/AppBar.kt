package com.lmar.checkersgame.presentation.common.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarState
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppBar(
    title: String,
    onBackAction: () -> Unit,
    state: TopAppBarState = rememberTopAppBarState()
) {
    TopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        title = { Text(title, color = MaterialTheme.colorScheme.primary, fontSize = 18.sp) },
        navigationIcon = {
            IconButton(
                onClick = { onBackAction() },
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Volver",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        },
        actions = {
        },
        scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(
            state
        )
    )
}