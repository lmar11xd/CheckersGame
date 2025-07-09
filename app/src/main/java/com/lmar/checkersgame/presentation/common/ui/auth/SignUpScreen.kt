package com.lmar.checkersgame.presentation.common.ui.auth

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.lmar.checkersgame.R
import com.lmar.checkersgame.presentation.common.components.AppBar
import com.lmar.checkersgame.presentation.common.components.DividerTextComponent
import com.lmar.checkersgame.presentation.common.components.FormCheckbox
import com.lmar.checkersgame.presentation.common.components.FormPasswordTextField
import com.lmar.checkersgame.presentation.common.components.FormTextField
import com.lmar.checkersgame.presentation.common.components.NormalTextComponent
import com.lmar.checkersgame.presentation.common.components.ShadowText
import com.lmar.checkersgame.presentation.common.viewmodel.auth.AuthState
import com.lmar.checkersgame.presentation.common.viewmodel.auth.AuthViewModel
import com.lmar.checkersgame.presentation.navigation.AppRoutes

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignUpScreen(
    navController: NavController,
    authViewModel: AuthViewModel = hiltViewModel()
) {
    val snackbarHostState = remember { SnackbarHostState() }

    val authState = authViewModel.authState.observeAsState()

    val context = LocalContext.current

    var names by remember {
        mutableStateOf("")
    }

    var email by remember {
        mutableStateOf("")
    }

    var password by remember {
        mutableStateOf("")
    }

    var passwordRepeat by remember {
        mutableStateOf("")
    }

    LaunchedEffect(authState.value) {
        when(authState.value) {
            AuthState.Authenticated -> navController.navigate(AppRoutes.HomeScreen.route)
            is AuthState.Error -> {
                Toast.makeText(context, (authState.value as AuthState.Error).message, Toast.LENGTH_SHORT).show()
            }
            else -> Unit
        }
    }

    Scaffold(
        topBar = {
            AppBar(
                "Registrarse",
                onBackAction = {
                    navController.popBackStack()
                },
                state = rememberTopAppBarState()
            )
        },
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .padding(paddingValues)
        ) {
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
                        text = "Crear una Cuenta,",
                        fontFamily = MaterialTheme.typography.displayLarge.fontFamily!!,
                        fontSize = 32.sp,
                        textAlign = TextAlign.Start,
                        textColor = MaterialTheme.colorScheme.primary,
                        shadowColor = MaterialTheme.colorScheme.primary
                    )

                    NormalTextComponent(
                        "¡Regístrate para comenzar!",
                        textAlign = TextAlign.Start,
                        fontSize = 16.sp,
                        modifier = Modifier.padding(horizontal = 5.dp)
                    )
                }

                Spacer(modifier = Modifier.height(18.dp))

                FormTextField(
                    value = names,
                    label = "Nombres",
                    icon = Icons.Default.Person,
                    onValueChange = {
                        names = it
                    }
                )

                Spacer(modifier = Modifier.height(8.dp))

                FormTextField(
                    value = email,
                    label = "Correo",
                    icon = Icons.Default.Email,
                    onValueChange = {
                        email = it
                    }
                )

                Spacer(modifier = Modifier.height(8.dp))

                FormPasswordTextField(
                    value = password,
                    label = "Contraseña",
                    icon = Icons.Default.Lock,
                    onValueChange = {
                        password = it
                    }
                )

                Spacer(modifier = Modifier.height(8.dp))

                FormPasswordTextField(
                    value = passwordRepeat,
                    label = "Contraseña",
                    icon = Icons.Default.Lock,
                    onValueChange = {
                        passwordRepeat = it
                    }
                )

                Spacer(modifier = Modifier.height(8.dp))
                FormCheckbox(value = stringResource(R.string.terms_and_conditions))

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = {
                        authViewModel.signup(names, email, password, passwordRepeat)
                    },
                    enabled = authState.value != AuthState.Loading,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Registrarse")
                }

                Spacer(modifier = Modifier.height(24.dp))

                DividerTextComponent()

                Row(
                    modifier = Modifier.padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("¿Ya tienes una cuenta? ", color = Color.Gray)
                    Text(
                        text = "Login",
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.tertiary,
                        modifier = Modifier
                            .padding(horizontal = 4.dp)
                            .clickable {
                                navController.navigate(AppRoutes.LoginScreen.route)
                            }
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun SignUpScreenPreview() {
    SignUpScreen(rememberNavController())
}