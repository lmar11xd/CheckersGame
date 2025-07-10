package com.lmar.checkersgame.presentation.common.ui.auth

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.rememberAsyncImagePainter
import com.lmar.checkersgame.R
import com.lmar.checkersgame.presentation.common.components.AppBar
import com.lmar.checkersgame.presentation.common.components.FormTextField
import com.lmar.checkersgame.presentation.common.components.GlowingCard
import com.lmar.checkersgame.presentation.common.components.HeadingTextComponent
import com.lmar.checkersgame.presentation.common.components.ImageCircle
import com.lmar.checkersgame.presentation.common.components.Loading
import com.lmar.checkersgame.presentation.common.components.NormalTextComponent
import com.lmar.checkersgame.presentation.common.viewmodel.auth.AuthState
import com.lmar.checkersgame.presentation.common.viewmodel.auth.AuthViewModel
import com.lmar.checkersgame.presentation.common.viewmodel.auth.ProfileViewModel
import com.lmar.checkersgame.presentation.navigation.AppRoutes
import com.lmar.checkersgame.core.utils.Constants.PHOTO_SIZE

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    navController: NavController,
    profileViewModel: ProfileViewModel = hiltViewModel(),
    authViewModel: AuthViewModel = hiltViewModel()
) {
    val snackbarHostState = remember { SnackbarHostState() }

    val authState = authViewModel.authState.observeAsState()
    val userState = profileViewModel.userState.observeAsState()

    val showForm by profileViewModel.showForm.observeAsState()

    val isLoading by profileViewModel.isLoading.observeAsState()

    val profileImageUri by profileViewModel.profileImageUri.observeAsState()

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        profileViewModel.setProfileImage(uri)
    }

    Scaffold(
        topBar = {
            AppBar(
                "Perfil",
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
            ) {
                Spacer(modifier = Modifier.size(16.dp))

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    if (authState.value == AuthState.Authenticated) {
                        //Información del Usuario
                        Box(modifier = Modifier.size(PHOTO_SIZE)) {
                            GlowingCard(
                                modifier = Modifier
                                    .size(PHOTO_SIZE)
                                    .padding(5.dp),
                                glowingColor = MaterialTheme.colorScheme.tertiary,
                                containerColor = MaterialTheme.colorScheme.tertiary,
                                cornerRadius = Int.MAX_VALUE.dp
                            ) {
                                if(profileImageUri == null) {
                                    userState.value?.imageUrl?.let { imageUrl ->
                                        if(imageUrl.isEmpty()) {
                                            ImageCircle(
                                                painter = painterResource(R.drawable.default_avatar),
                                                modifier = Modifier.size(PHOTO_SIZE)
                                            )
                                        } else {
                                            ImageCircle(
                                                imageUrl = imageUrl,
                                                modifier = Modifier.size(PHOTO_SIZE)
                                            )
                                        }
                                    }
                                } else {
                                    ImageCircle(
                                        painter = rememberAsyncImagePainter(profileImageUri),
                                        modifier = Modifier.size(PHOTO_SIZE)
                                    )
                                }
                            }

                            if (showForm == true) {
                                IconButton(
                                    onClick = { launcher.launch("image/*") },
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clip(CircleShape)
                                        .background(MaterialTheme.colorScheme.tertiary)
                                        .align(Alignment.CenterEnd)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.CameraAlt,
                                        contentDescription = "Camara",
                                        tint = Color.White,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.size(8.dp))

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 32.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically, // Alinea los elementos verticalmente
                            horizontalArrangement = Arrangement.SpaceBetween // Distribuye los elementos en la fila
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                HeadingTextComponent(
                                    value = userState.value?.names ?: "",
                                    textColor = MaterialTheme.colorScheme.primary,
                                    fontSize = 16.sp
                                )

                                NormalTextComponent(
                                    value = userState.value?.email ?: "",
                                    fontSize = 14.sp
                                )
                            }

                            if (showForm == false) {
                                IconButton(
                                    onClick = { profileViewModel.showForm() },
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clip(CircleShape)
                                        .background(MaterialTheme.colorScheme.primary)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Edit,
                                        contentDescription = "Edit",
                                        tint = Color.White,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                            } else {
                                IconButton(
                                    onClick = { profileViewModel.dismissForm() },
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clip(CircleShape)
                                        .background(MaterialTheme.colorScheme.tertiary)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Edit",
                                        tint = Color.White,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        if (showForm == true) {
                            //Formulario
                            FormTextField(
                                value = userState.value?.names ?: "",
                                label = "Nombres",
                                icon = Icons.Default.Person,
                                onValueChange = {
                                    profileViewModel.changeName(it)
                                }
                            )
                        }
                    } else if (authState.value == AuthState.Unauthenticated) {
                        //Default
                        GlowingCard(
                            modifier = Modifier
                                .size(PHOTO_SIZE)
                                .padding(5.dp),
                            glowingColor = MaterialTheme.colorScheme.tertiary,
                            containerColor = MaterialTheme.colorScheme.tertiary,
                            cornerRadius = Int.MAX_VALUE.dp
                        ) {
                            Image(
                                painter = painterResource(R.drawable.default_avatar),
                                contentDescription = "Logo",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .size(PHOTO_SIZE)
                                    .clip(CircleShape)
                                    .border(5.dp, MaterialTheme.colorScheme.tertiary, CircleShape),
                            )
                        }

                        Spacer(modifier = Modifier.size(8.dp))

                        NormalTextComponent(
                            "¡Inicia sesión y juega en línea con tus amigos!",
                            fontSize = 14.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.size(18.dp))

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Bottom
                ) {
                    if (showForm == true) {
                        Spacer(modifier = Modifier.size(4.dp))

                        Button(
                            onClick = {
                                profileViewModel.saveForm()
                            },
                            colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.primary),
                            modifier = Modifier.width(200.dp)
                        ) {
                            Text("Guardar")
                        }
                    }

                    Spacer(modifier = Modifier.size(4.dp))

                    if (authState.value == AuthState.Authenticated) {
                        OutlinedButton(
                            onClick = { authViewModel.signout() },
                            modifier = Modifier.width(200.dp)
                        ) {
                            Text("Cerrar Sesión")
                        }
                    } else if (authState.value == AuthState.Unauthenticated) {
                        Button(
                            onClick = {
                                navController.navigate(AppRoutes.LoginScreen.route)
                            },
                            colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.tertiary),
                            modifier = Modifier.width(200.dp)
                        ) {
                            Text("Iniciar Sesión")
                        }

                        Spacer(modifier = Modifier.size(4.dp))

                        Button(
                            onClick = {
                                navController.navigate(AppRoutes.SignUpScreen.route)
                            },
                            colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.primary),
                            modifier = Modifier.width(200.dp)
                        ) {
                            Text("Registrarse")
                        }
                    }
                }

                Spacer(modifier = Modifier.size(4.dp))
            }
        }

        if (isLoading == true) {
            Loading()
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ProfileScreenPreview() {
    ProfileScreen(rememberNavController())
}