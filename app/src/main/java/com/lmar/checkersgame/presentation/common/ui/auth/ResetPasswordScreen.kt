package com.lmar.checkersgame.presentation.common.ui.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.lmar.checkersgame.R
import com.lmar.checkersgame.core.ui.theme.CheckersGameTheme
import com.lmar.checkersgame.presentation.common.components.AppBar
import com.lmar.checkersgame.presentation.common.components.FormTextField
import com.lmar.checkersgame.presentation.common.components.GradientButton
import com.lmar.checkersgame.presentation.common.components.Loading
import com.lmar.checkersgame.presentation.common.components.NormalTextComponent
import com.lmar.checkersgame.presentation.common.components.ShadowText
import com.lmar.checkersgame.presentation.common.event.ResetPasswordEvent
import com.lmar.checkersgame.presentation.common.viewmodel.auth.ResetPasswordViewModel
import com.lmar.checkersgame.presentation.navigation.handleUiEvents

@Composable
fun ResetPasswordScreenContainer(
    navController: NavHostController,
    resetPasswordViewModel: ResetPasswordViewModel = hiltViewModel()
) {

    val email by resetPasswordViewModel.email.collectAsState()
    val isLoading by resetPasswordViewModel.isLoading.collectAsState()

    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        navController.handleUiEvents(
            scope = coroutineScope,
            uiEventFlow = resetPasswordViewModel.eventFlow
        )
    }

    ResetPasswordScreen(
        email = email,
        isLoading = isLoading,
        onEvent = {
            resetPasswordViewModel.onEvent(it)
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResetPasswordScreen(
    email: String = "",
    isLoading: Boolean = false,
    onEvent: (ResetPasswordEvent) -> Unit = {}
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        Image(
            painter = painterResource(id = R.drawable.bg1),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxSize()
        )

        Column {
            AppBar(
                "Reestablecer Contraseña",
                onBackAction = { onEvent(ResetPasswordEvent.ToBack) },
                state = rememberTopAppBarState()
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 32.dp, vertical = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    ShadowText(
                        text = "Hola,",
                        fontFamily = MaterialTheme.typography.displayLarge.fontFamily!!,
                        fontSize = 32.sp,
                        textAlign = TextAlign.Start,
                        textColor = MaterialTheme.colorScheme.onPrimary,
                        shadowColor = MaterialTheme.colorScheme.primary
                    )

                    NormalTextComponent(
                        "Ingresa tu correo electrónico y te enviaremos un enlace para restablecer tu contraseña",
                        textAlign = TextAlign.Start,
                        textColor = MaterialTheme.colorScheme.tertiary,
                        fontSize = 16.sp,
                        modifier = Modifier.padding(horizontal = 5.dp)
                    )
                }

                Spacer(modifier = Modifier.height(18.dp))

                FormTextField(
                    value = email,
                    label = "Correo",
                    icon = Icons.Default.Email,
                    onValueChange = { onEvent(ResetPasswordEvent.EnteredEmail(it)) }
                )

                Spacer(modifier = Modifier.height(24.dp))

                GradientButton(
                    text = "Enviar",
                    onClick = { onEvent(ResetPasswordEvent.ResetPassword) },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }

    if (isLoading) {
        Loading()
    }
}

@Preview(showBackground = true)
@Composable
private fun ResetPasswordScreenPreview() {
    CheckersGameTheme {
        ResetPasswordScreen()
    }
}