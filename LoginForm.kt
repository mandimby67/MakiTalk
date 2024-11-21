package mg.gasydev.tenymalagasy.presentation.ui.components.form

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Password
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import mg.gasydev.tenymalagasy.R
import mg.gasydev.tenymalagasy.data.model.api.response.AuthUser
import mg.gasydev.tenymalagasy.domain.enumType.InscriptionFieldType
import mg.gasydev.tenymalagasy.domain.result.AuthResult
import mg.gasydev.tenymalagasy.presentation.ui.components.button.RoundedButton
import mg.gasydev.tenymalagasy.presentation.ui.components.textfield.CustomTextField
import mg.gasydev.tenymalagasy.presentation.ui.navigation.Graph
import mg.gasydev.tenymalagasy.presentation.ui.theme.DarkGreen
import mg.gasydev.tenymalagasy.presentation.ui.theme.GreenBackgroundColor
import mg.gasydev.tenymalagasy.presentation.viewmodel.auth.AuthViewModel
import mg.gasydev.tenymalagasy.utils.ValidationUtils

@Composable
fun LoginForm(
    rootNavController: NavController = rememberNavController(),
    formTitle: String,
    authViewModel: AuthViewModel,
    onLoginSuccess: (authUser: AuthUser?) -> Unit = {}
) {

    var emailState by remember { mutableStateOf("") }
    var emailMessage by remember { mutableStateOf("") }
    var passwordState by remember { mutableStateOf("") }
    var message by remember { mutableStateOf("") }
    var isError by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    var isBtnConnexionEnabled by remember { mutableStateOf(false) }

    val context = LocalContext.current

    LaunchedEffect(emailState, passwordState) {
        // Enlever le message d'erreur du mail dès que le format est valide (ne pas attendre le clique sur le bouton)
        if (ValidationUtils.isValidEmail(emailState)) {
            emailMessage = ""
        }
        // Mettre le bouton de connexion actif si les champs sont remplis
        isBtnConnexionEnabled = !(emailState.isEmpty() || passwordState.isEmpty())
    }

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        if (formTitle.isNotEmpty()) {
            Spacer(modifier = Modifier.height(7.dp))
            Text(
                modifier = Modifier.padding(start = 7.dp),
                text = formTitle,
                style = MaterialTheme.typography.bodySmall,
                fontSize = 15.sp,
                color = DarkGreen,
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        CustomTextField(
            imageVector = Icons.Default.Email,
            textState = emailState,
            onTextChange = { newText -> emailState = newText },
            label = context.getString(R.string.auth_compte_connexion_email),
            backgroundColor = Color.White,
            cursorColor = GreenBackgroundColor,
            textColor = if (emailMessage.isNotEmpty() && emailState.isNotEmpty() && !ValidationUtils.isValidEmail(emailState)) Color.Red else GreenBackgroundColor,
            textFieldColor = Color.Gray,
            borderColor = if (emailMessage.isNotEmpty() && emailState.isNotEmpty() && !ValidationUtils.isValidEmail(emailState)) Color.Red else GreenBackgroundColor,
            borderSize = 1.2.dp,
            fieldType = InscriptionFieldType.EMAIL
        )
        // Afficher le message d'erreur ou de succès
        if (emailMessage.isNotEmpty() && emailState.isNotEmpty()) {
            Text(
                modifier = Modifier.padding(start = 7.dp),
                text = emailMessage,
                color = Color.Red,
                style = MaterialTheme.typography.bodySmall
            )
        }
        Spacer(modifier = Modifier.height(12.dp))
        CustomTextField(
            imageVector = Icons.Default.Password,
            textState = passwordState,
            onTextChange = { newText -> passwordState = newText },
            label = context.getString(R.string.auth_compte_connexion_mdp),
            backgroundColor = Color.White,
            cursorColor = GreenBackgroundColor,
            textColor = GreenBackgroundColor,
            textFieldColor = Color.Gray,
            borderSize = 1.2.dp,
            fieldType = InscriptionFieldType.PASSWORD
        )

        // Afficher le message d'erreur ou de succès
        if (message.isNotEmpty()) {
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                modifier = Modifier.padding(start = 7.dp),
                text = message,
                color = if (isError) Color.Red else GreenBackgroundColor,
                style = MaterialTheme.typography.bodySmall
            )
            Spacer(modifier = Modifier.height(12.dp))
        } else {
            Spacer(modifier = Modifier.height(20.dp))
        }

        RoundedButton(
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            onClick = {
                if (ValidationUtils.isValidEmail(emailState)) {
                    // Loading
                    isLoading = true
                    // Appeler la fonction de connexion
                    authViewModel.signInWithEmailAndPassword(
                        emailState,
                        passwordState
                    ) { result ->
                        when (result) {
                            is AuthResult.Success -> {
                                Log.i("DEBUG_AUTH", "Success")
                                message = context.getString(R.string.auth_compte_connexion_reussi)
                                isError = false
                                isLoading = false
                                // Rediriger vers un autre écran
                                onLoginSuccess(result.data)
                            }
                            is AuthResult.Failure -> {
                                Log.i("DEBUG_AUTH", "Failure")
                                message = result.error.message.toString()
                                isError = true
                                isLoading = false
                            }
                            AuthResult.InvalidCredentials -> {
                                Log.i("DEBUG_AUTH", "InvalidCredentials")
                                message = context.getString(R.string.auth_compte_connexion_erreur)
                                isError = true
                                isLoading = false
                            }
                            AuthResult.NetworkException -> {
                                Log.i("DEBUG_AUTH", "NetworkException")
                                message = context.getString(R.string.auth_compte_connexion_erreur_serveur)
                                isError = true
                                isLoading = false
                            }
                            AuthResult.Unauthorized -> {
                                Log.i("DEBUG_AUTH", "Unauthorized")
                                message = context.getString(R.string.auth_compte_connexion_unauthorized)
                                isError = true
                                isLoading = false
                            }
                            AuthResult.EmailExist -> {
                                isError = true
                                isLoading = false
                            }
                            else -> {}
                        }
                    }
                } else {
                    emailMessage = context.getString(R.string.auth_compte_connexion_email_invalide)
                }
            },
            colorNotClicked = if (isBtnConnexionEnabled) GreenBackgroundColor else GreenBackgroundColor.copy(0.4f),
            colorClicked = GreenBackgroundColor,
            isLoading = isLoading,
            enabled = isBtnConnexionEnabled
        ) {
            Text(
                text = context.getString(R.string.auth_compte_connexion_btn),
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White
            )
        }

        Spacer(modifier = Modifier.height(9.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(indication = null, interactionSource = remember { MutableInteractionSource() }) {
                    rootNavController.navigate(Graph.FORGOT_PASSWORD)
                },
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
            )
            Text(
                text = context.getString(R.string.auth_compte_connexion_mdp_oublier),
                style = MaterialTheme.typography.displaySmall,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                fontSize = 12.sp,
                lineHeight = 25.sp,
                color = Color.Gray,
                textDecoration = TextDecoration.Underline
            )
        }
    }

}