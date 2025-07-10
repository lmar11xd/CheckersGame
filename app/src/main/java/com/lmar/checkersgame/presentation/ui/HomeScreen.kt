package com.lmar.checkersgame.presentation.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.lmar.checkersgame.R
import com.lmar.checkersgame.core.ui.theme.CheckersGameTheme
import com.lmar.checkersgame.presentation.common.components.GlowingCard
import com.lmar.checkersgame.presentation.common.components.ShadowText
import com.lmar.checkersgame.presentation.common.viewmodel.auth.AuthState
import com.lmar.checkersgame.presentation.common.viewmodel.auth.AuthViewModel
import com.lmar.checkersgame.presentation.navigation.AppRoutes

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    authViewModel: AuthViewModel = hiltViewModel()
) {
    val snackbarHostState = remember { SnackbarHostState() }

    val authState = authViewModel.authState.observeAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                title = {
                    Text("")
                },
                actions = {
                    IconButton(
                        onClick = { navController.navigate(AppRoutes.ProfileScreen.route) },
                    ) {
                        Icon(
                            imageVector = Icons.Default.AccountCircle,
                            contentDescription = "Configuraciones",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(
                    rememberTopAppBarState()
                )
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .padding(paddingValues),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            ShadowText(
                text = stringResource(R.string.app_name),
                fontFamily = MaterialTheme.typography.displayLarge.fontFamily!!,
                fontSize = 32.sp,
                textColor = MaterialTheme.colorScheme.primary,
                shadowColor = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(16.dp))

            GlowingCard(
                modifier = Modifier
                    .size(100.dp)
                    .padding(5.dp),
                glowingColor = MaterialTheme.colorScheme.primary,
                containerColor = MaterialTheme.colorScheme.primary,
                cornerRadius = Int.MAX_VALUE.dp
            ) {
                Image(
                    painter = painterResource(R.drawable.checkers),
                    contentDescription = "Logo",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape)
                        .border(5.dp, MaterialTheme.colorScheme.primary, CircleShape)
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = {
                    navController.navigate(AppRoutes.SingleGameScreen.route)
                },
                colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.tertiary),
                modifier = Modifier.width(200.dp)
            ) {
                Text("Un Jugador")
            }

            Spacer(modifier = Modifier.size(4.dp))

            if (authState.value == AuthState.Authenticated) {
                Button(
                    onClick = {
                        navController.navigate(AppRoutes.RoomScreen.route)
                    },
                    colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.primary),
                    modifier = Modifier.width(200.dp)
                ) {
                    Text("Multijugador")
                }

                Spacer(modifier = Modifier.size(4.dp))
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    CheckersGameTheme {
        HomeScreen(rememberNavController())
    }
}