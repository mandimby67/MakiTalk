package mg.gasydev.tenymalagasy.presentation.ui.screens.home.profile

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.MilitaryTech
import androidx.compose.material.icons.filled.NoAccounts
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.FirebaseUser
import mg.gasydev.tenymalagasy.R
import mg.gasydev.tenymalagasy.domain.enumType.EmailSendStatus
import mg.gasydev.tenymalagasy.domain.enumType.EmailSendType
import mg.gasydev.tenymalagasy.domain.enumType.InscriptionFieldType
import mg.gasydev.tenymalagasy.domain.result.AuthResult
import mg.gasydev.tenymalagasy.presentation.ui.components.button.BackButton
import mg.gasydev.tenymalagasy.presentation.ui.components.button.RoundedButton
import mg.gasydev.tenymalagasy.presentation.ui.components.group.dialog.ConditionDialog
import mg.gasydev.tenymalagasy.presentation.ui.components.group.dialog.EmailExistDialog
import mg.gasydev.tenymalagasy.presentation.ui.components.group.dialog.SimpleButtonDialog
import mg.gasydev.tenymalagasy.presentation.ui.components.group.image.CircleimageWithBorder
import mg.gasydev.tenymalagasy.presentation.ui.components.group.image.ProfileAvatarSelect
import mg.gasydev.tenymalagasy.presentation.ui.components.sheet.OverlaySheet
import mg.gasydev.tenymalagasy.presentation.ui.components.sheet.PasswordBottomSheet
import mg.gasydev.tenymalagasy.presentation.ui.components.textfield.CustomTextField
import mg.gasydev.tenymalagasy.presentation.ui.navigation.Graph
import mg.gasydev.tenymalagasy.presentation.ui.screens.auth.SendEmailScreen
import mg.gasydev.tenymalagasy.presentation.ui.theme.ButtonActiveColor
import mg.gasydev.tenymalagasy.presentation.ui.theme.GreenBackgroundColor
import mg.gasydev.tenymalagasy.presentation.viewmodel.home.profile.ProfileEditViewModel
import mg.gasydev.tenymalagasy.presentation.viewmodel.service.CountdownViewModel
import mg.gasydev.tenymalagasy.utils.ValidationUtils

@Composable
fun ProfileEditScreen(
    rootNavController: NavController = rememberNavController(),
    countdownViewModel: CountdownViewModel,
    onEditSuccess: (authUser: FirebaseUser?) -> Unit,
    onEditCancel: () -> Unit,
) {

    // context
    val context = LocalContext.current

    // ViewModel
    val profileEditViewModel: ProfileEditViewModel = hiltViewModel()
    val userData = profileEditViewModel.getUserDataFromPreferences()

    // Conserver les valeurs initiales
    val initialName = userData.displayName ?: ""
    val initialEmail = userData.email ?: ""
    val initialImage = userData.photoURL ?: ""

    // variable
    var showAvatarSelection by remember { mutableStateOf(false) }
    var focusedTextField by remember { mutableIntStateOf(0) }
    var nameState by remember { mutableStateOf(initialName) }
    var emailState by remember { mutableStateOf(initialEmail) }
    var emailMessage by remember { mutableStateOf("") } // erreur que pour le mail
    var confirmedSelectedImageState by remember { mutableStateOf(initialImage) }
    var isButtonEnregistrerEnabled by remember { mutableStateOf(false) }

    var message by remember { mutableStateOf("") } // erreur plus generale (globale)

    // dialog info pour le clique sur difficulte et tranche d'age
    var currentClick by remember { mutableStateOf("") }
    var isShowDialogInfo by remember { mutableStateOf(false) }

    // Saisie mot de passe avant d'effectuer une modification sur firebase (obligation d'etre loggé dans les 5 mn passé)
    var isPasswordVisible by remember { mutableStateOf(false) }

    // dialog de confirmation pour la suppression de compte
    var isShowDialogDelete by remember { mutableStateOf(false) }

    // Activer le bouton uniquement si les valeurs ont changé par rapport aux valeurs initiales
    //isButtonEnregistrerEnabled = (nameState.isNotEmpty() && nameState != initialName) || (emailState.isNotEmpty() && emailState != initialEmail) || confirmedSelectedImageState != initialImage
    isButtonEnregistrerEnabled =
        (nameState.isNotEmpty() && emailState.isNotEmpty()) &&
                ((nameState != initialName) || (emailState != initialEmail) || (confirmedSelectedImageState != initialImage))

    // FocusManager pour gérer le focus
    val focusManager = LocalFocusManager.current
    // FocusRequester pour demander le focus sur un composant spécifique
    val focusRequesterName = FocusRequester()
    val focusRequesterEmail = FocusRequester()

    // images
    val imageUrls by profileEditViewModel.imageUrls.collectAsState()
    val selectedImageUrl by profileEditViewModel.selectedImageUrl.collectAsState()
    val isLoadingImage by profileEditViewModel.isLoadingImage.collectAsState()
    val isImagesError by profileEditViewModel.isErrorMessageImage.collectAsState()

    // resultat obtenu lors de la modification de l'utilisateur
    var isVerificationEmailVisible by remember { mutableStateOf(false) }
    var isExistEmailVisible by remember { mutableStateOf(false) }
    val updateState by profileEditViewModel.updateState.collectAsState()
    val isLoadingUpdate by profileEditViewModel.isLoadingUpdate.collectAsState()

    // resultat lors de la suppression de l'utilisateur
    var currentStatus by remember { mutableStateOf(ClickCurrentStatus.IDLE) }
    val deleteState by profileEditViewModel.deleteState.collectAsState()
    val isLoadingDelete by profileEditViewModel.isLoadingDelete.collectAsState()

    // variable d'affichage du dialog d'envoi de mail de verification (SendEmailScreen deja affiché => mail envoyé)
    var isShowDialogResend by remember { mutableStateOf(false) }

    // statut de l'envoi du mail de verification pour l'icon sur le dialog (loading si chargement, check si envoyé, ...)
    val emailVerificationStatus by profileEditViewModel.emailVerificationStatus.collectAsState()

    // gestion des appels multiple des resultat de requete
    var isHandled by remember { mutableStateOf(false) }

    // Gestion scroll pour le clavier qui compresse le textField Email
    val scrollState = rememberScrollState()

    // local function
    fun resetUpdateWithUI() {
        // Cacher le bottom sheet de saisie de mdp
        isPasswordVisible = false
        // Changer l'etat de la requete por eviter de rentrer en boucle dedans
        profileEditViewModel.resetUpdateState()
        // Marquer comme traité
        isHandled = true
    }

    fun resetDeleteWithUI() {
        // Cacher le bottom sheet de saisie de mdp
        isPasswordVisible = false
        // Changer l'etat de la requete por eviter de rentrer en boucle dedans
        profileEditViewModel.resetDeleteState()
    }

    // Gestion UPDATE
    if (!isHandled) {
        when (val result = updateState) {
            is AuthResult.Success -> {
                //Log.i("DEBUG_NAVIG", "==>SUCCESS UPDATE")
                onEditSuccess(result.data)
                resetUpdateWithUI()
            }

            is AuthResult.VerificationEmailSent -> {
                //Log.i("DEBUG_NAVIG", "==>VerificationEmailSent")
                // affiche l'ecran qui confirme que l'email a été envoyé
                isVerificationEmailVisible = true
                // effacer les données utilisateur pour le forcer a se deconnecter (se reconnecter apres)
                profileEditViewModel.clearUserPreferences()
                // active le countdown de 5-10 minutes (intervalle de non possibilité d'envoi de mail)
                countdownViewModel.startService(true)
                resetUpdateWithUI()
            }

            is AuthResult.EmailExist -> {
                //Log.i("DEBUG_NAVIG", "==>EmailExist")
                // Affiche le message comme quoi le mail existe déjà et echec de modification
                isExistEmailVisible = true
                resetUpdateWithUI()
            }

            is AuthResult.Failure -> {
                //Log.i("DEBUG_NAVIG", "==>Failure UPDATE")
                message = result.error.message.toString()
                resetUpdateWithUI()
            }

            AuthResult.InvalidCredentials -> {
                //Log.i("DEBUG_NAVIG", "InvalidCredentials UPDATE")
                message = context.getString(R.string.auth_compte_connexion_erreur)
                resetUpdateWithUI()
            }

            AuthResult.NetworkException -> {
                //Log.i("DEBUG_NAVIG", "NetworkException UPDATE")
                message = context.getString(R.string.auth_compte_connexion_erreur_serveur)
                resetUpdateWithUI()
            }

            else -> { /* Affiche un état initial ou rien */
            }
        }
    }

    // Gestion DELETE
    when (val result = deleteState) {
        is AuthResult.Success -> {
            //Log.i("DEBUG_NAVIG", "==>SUCCESS DELETE")
            resetDeleteWithUI()
            profileEditViewModel.clearUserPreferences()
            rootNavController.navigate(Graph.SPLASH) {
                popUpTo(0) { inclusive = true } // Efface tout le backstack
                launchSingleTop = true // Évite de recréer la destination si elle est déjà au sommet
            }
        }
        is AuthResult.Failure -> {
            //Log.i("DEBUG_NAVIG", "==>Failure DELETE")
            message = result.error.message.toString()
            resetDeleteWithUI()
        }
        AuthResult.InvalidCredentials -> {
            //Log.i("DEBUG_NAVIG", "InvalidCredentials DELETE")
            message = context.getString(R.string.auth_compte_connexion_erreur)
            resetDeleteWithUI()
        }
        AuthResult.NetworkException -> {
            //Log.i("DEBUG_NAVIG", "NetworkException DELETE")
            message = context.getString(R.string.auth_compte_connexion_erreur_serveur)
            resetDeleteWithUI()
        }
        else -> {}
    }

    LaunchedEffect(emailState) {
        // Enlever le message d'erreur du mail dès que le format est valide (ne pas attendre le clique sur le bouton)
        if (ValidationUtils.isValidEmail(emailState)) {
            emailMessage = ""
        }
    }

    // Eviter weight pour faire marcher le scroll
    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp.dp // Hauteur totale de l'écran
    val boxHeight = screenHeight / 3 // 1/3 de la hauteur totale

    Box (
        modifier = Modifier
            .fillMaxSize()
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ) {
                // Retirer le focus de tous les TextFields lorsque l'utilisateur clique en dehors
                focusManager.clearFocus()
            }
            .padding(WindowInsets.ime.asPaddingValues()) // Ajuste la marge selon le clavier
    ) {
        Column (
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(scrollState)
                .padding(horizontal = 25.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(30.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(boxHeight)
                    //.background(Color.Red)
            ) {
                Box(
                    modifier = Modifier
                        .align(Alignment.Center)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box (
                            modifier = Modifier.clickable(
                                indication = null,
                                interactionSource = remember { MutableInteractionSource() }
                            ) {
                                focusManager.clearFocus()
                                showAvatarSelection = true
                                profileEditViewModel.fetchImageUrls()
                            }
                        ) {
                            CircleimageWithBorder(
                                imageUrl = confirmedSelectedImageState,
                                borderColor = GreenBackgroundColor,
                                borderWidth = (1.2).dp,
                                imageSize = 120.dp
                            )
                            // Icon circle at bottom-right with a slight offset
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .align(Alignment.BottomEnd)
                                    .offset(
                                        x = (-8).dp,
                                        y = (-8).dp
                                    )
                                    .background(GreenBackgroundColor, shape = CircleShape)
                                    .border(1.2.dp, Color.White, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Edit,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(
                            modifier = Modifier.clickable(
                                indication = null,
                                interactionSource = remember { MutableInteractionSource() }
                            ) {
                                currentClick = ClickEditStatus.DIFFICULTY.displayed
                                isShowDialogInfo = true
                            },
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                modifier = Modifier.size(20.dp),
                                imageVector = Icons.Default.MilitaryTech,
                                contentDescription = null,
                                tint = ButtonActiveColor
                            )
                            Text(
                                modifier = Modifier.padding(horizontal = 7.dp),
                                text = "Débutant",
                                style = MaterialTheme.typography.bodyMedium,
                                fontSize = 14.sp,
                                color = GreenBackgroundColor
                            )
                        }

                    }
                }
            }



            Box(modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                //.background(Color.Green)
            ) {
                Column (
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    CustomTextField(
                        modifier = Modifier
                            .onFocusChanged { focusState ->
                                if (focusState.isFocused) {
                                    focusedTextField = 1
                                } else if (focusedTextField == 1) {
                                    focusedTextField =
                                        -1 // Le focus est perdu, mettre à jour l'état
                                }
                            }
                            .focusRequester(focusRequesterName),
                        imageVector = Icons.Default.Person,
                        textState = nameState,
                        onTextChange = { newText -> nameState = newText },
                        label = context.getString(R.string.profile_champ_nom_complet),
                        backgroundColor = Color.White,
                        cursorColor = GreenBackgroundColor,
                        textColor = GreenBackgroundColor,
                        textFieldColor = Color.Gray,
                        borderSize = 1.2.dp,
                        fieldType = InscriptionFieldType.NAME,
                    )
                    Spacer(modifier = Modifier.height(17.dp))
                    Column(
                        modifier = Modifier
                            .fillMaxWidth(),
                    ) {
                        CustomTextField(
                            modifier = Modifier
                                .onFocusChanged { focusState ->
                                    if (focusState.isFocused) {
                                        focusedTextField = 2
                                    } else if (focusedTextField == 2) {
                                        focusedTextField =
                                            -1 // Le focus est perdu, mettre à jour l'état
                                    }
                                }
                                .focusRequester(focusRequesterEmail),
                            imageVector = Icons.Default.Email,
                            textState = emailState,
                            onTextChange = { newText -> emailState = newText },
                            label = context.getString(R.string.profile_champ_email),
                            backgroundColor = Color.White,
                            cursorColor = GreenBackgroundColor,
                            textColor = if (emailMessage.isNotEmpty() && emailState.isNotEmpty() && !ValidationUtils.isValidEmail(emailState)) Color.Red else GreenBackgroundColor,
                            textFieldColor = Color.Gray,
                            borderColor = if (emailMessage.isNotEmpty() && emailState.isNotEmpty() && !ValidationUtils.isValidEmail(emailState)) Color.Red else GreenBackgroundColor,
                            borderSize = 1.2.dp,
                            fieldType = InscriptionFieldType.EMAIL,
                        )
                        if (emailMessage.isNotEmpty() && emailState.isNotEmpty()) {
                            Text(
                                modifier = Modifier.padding(start = 7.dp),
                                text = emailMessage,
                                color = Color.Red,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(17.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    //.background(Color.Gray)
            ) {

                Column (
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                ) {
                    // Afficher le message d'erreur plus global
                    if (message.isNotEmpty()) {
                        Text(
                            modifier = Modifier.padding(start = 7.dp),
                            text = message,
                            color = Color.Red ,
                            style = MaterialTheme.typography.bodySmall
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                    }
                    RoundedButton(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        onClick = {
                            if (ValidationUtils.isValidEmail(emailState)) {
                                //Log.i("DEBUG_NAVIG", "CLICKED UPDATE")
                                // Mentionner qu'il s'agit d'un UPDATE
                                currentStatus = ClickCurrentStatus.UPDATE
                                // Afficher un modal de saisie de mot de passe (obligatoire pour firebase)
                                isPasswordVisible = true
                                // Reset comme non traité (eviter d'appeler plusieurs fois les resultats)
                                isHandled = false
                            } else {
                                emailMessage = context.getString(R.string.auth_compte_connexion_email_invalide)
                            }
                        },
                        colorNotClicked = if (isButtonEnregistrerEnabled) GreenBackgroundColor else GreenBackgroundColor.copy(
                            0.6f
                        ),
                        colorClicked = GreenBackgroundColor,
                        enabled = isButtonEnregistrerEnabled,
                    ) {
                        Text(
                            text = context.getString(R.string.profile_champ_valider),
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White
                        )
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .clickable(
                                indication = null,
                                interactionSource = remember { MutableInteractionSource() }) {
                                // logique de suppression de compte (alert avant suppression)
                                isShowDialogDelete = true
                            },
                        textAlign = TextAlign.Center,
                        text = context.getString(R.string.profile_edit_remove_account),
                        color = GreenBackgroundColor,
                        style = MaterialTheme.typography.bodyMedium,
                        fontSize = 14.sp,
                        textDecoration = TextDecoration.Underline
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                }
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
        ) {

        }

        BackButton {
            onEditCancel()
        }

        // Overlay qui apparait avec le bottomsheet
        OverlaySheet(
            isVisible = isPasswordVisible,
            onDismissRequest = { isPasswordVisible = false }
        )

        // Bottomsheet pour saisir le mot de passe avant les modifs
        PasswordBottomSheet(
            modifier = Modifier.align(Alignment.BottomCenter),
            isVisible = isPasswordVisible,
            onValidateRequest = { password ->
                when(currentStatus) {
                    ClickCurrentStatus.UPDATE -> {
                        //Log.i("DEBUG_NAVIG", "CONFIRM UPDATE")
                        profileEditViewModel.updateUser(
                            nameState,
                            confirmedSelectedImageState,
                            emailState,
                            reauthEmail = initialEmail,
                            reauthPassword = password
                        )
                    }
                    ClickCurrentStatus.DELETE -> {
                        //Log.i("DEBUG_NAVIG", "CONFIRM DELETE")
                        profileEditViewModel.deleteUser(
                            uid = userData.uid ?: "",
                            reauthEmail = initialEmail,
                            reauthPassword = password
                        )
                    }
                    else -> {
                        Log.i("DEBUG_NAVIG", "IDLE")
                    }
                }
            },
            isLoading = if (currentStatus == ClickCurrentStatus.UPDATE) isLoadingUpdate else isLoadingDelete
        )

        AnimatedVisibility(
            visible = showAvatarSelection,
            enter = fadeIn(animationSpec = tween(durationMillis = 500)),
            exit = fadeOut(animationSpec = tween(durationMillis = 500))
        ) {
            ProfileAvatarSelect(
                imageUrls = imageUrls,
                onImageSelected = { url -> profileEditViewModel.onImageSelected(url) },
                onValidateSelection = { selectedImageUrl ->
                    if (selectedImageUrl != null) {
                        confirmedSelectedImageState = selectedImageUrl
                    }
                    showAvatarSelection = false
                },
                onDismiss = { showAvatarSelection = false },
                selectedImageUrl = selectedImageUrl,
                isLoading = isLoadingImage,
                isImagesError = isImagesError
            )
        }

        // dialogue pour les infos (clique sur difficulte et tranche d'age)
        if (isShowDialogInfo) {
            SimpleButtonDialog(
                imageVector = if (currentClick == ClickEditStatus.DIFFICULTY.displayed) Icons.Default.MilitaryTech else Icons.Default.Group,
                message = if (currentClick == ClickEditStatus.DIFFICULTY.displayed) context.getString(R.string.profile_edit_difficulty_message) else context.getString(R.string.profile_edit_agerange_message),
                onDismiss = {
                    isShowDialogInfo = false
                }
            )
        }

        ConditionDialog(
            showDialog = isShowDialogDelete,
            onDismiss = {
                isShowDialogDelete = false
            },
            onConfirm = {
                // Mentionner qu'il s'agit d'un DELETE
                currentStatus = ClickCurrentStatus.DELETE
                // Afficher un modal de saisie de mot de passe (obligatoire pour firebase)
                isPasswordVisible = true
            },
            message = context.getString(R.string.profile_edit_remove_account_message),
            isCgu = false,
            imageVector = Icons.Default.NoAccounts
        )

        // dialogue pour confirmer la suppresion de compte
        /*CustomConfirmationDialog(
            showDialog = isShowDialogDelete,
            onDismiss = {
                isShowDialogDelete = false
            },
            onConfirm = {
                // logique de suppressio de compte
                //Log.i("DEBUG_NAVIG", "CLICKED SUPPRESSION")
                // Mentionner qu'il s'agit d'un DELETE
                currentStatus = ClickCurrentStatus.DELETE
                // Afficher un modal de saisie de mot de passe (obligatoire pour firebase)
                isPasswordVisible = true
            },
            message = context.getString(R.string.profile_edit_remove_account_message),
            messageTextColor = disabledContentColor(),
            imageVector = Icons.Default.NoAccounts
        )*/

        // Erreur email existant
        AnimatedVisibility(
            visible = isExistEmailVisible,
            enter = fadeIn() + slideInVertically(initialOffsetY = { it / 4 }),
            exit = fadeOut() + slideOutVertically(targetOffsetY = { it / 4 })
        ) {
            EmailExistDialog(onDismiss = {
                onEditCancel()
            })
        }

        // Verification email
        AnimatedVisibility(
            visible = isVerificationEmailVisible,
            enter = fadeIn() + slideInVertically(initialOffsetY = { it / 4 }),
            exit = fadeOut() + slideOutVertically(targetOffsetY = { it / 4 })
        ) {
            SendEmailScreen(
                countdownViewModel = countdownViewModel,
                sendType = EmailSendType.VERIFICATION_MODIFICATION,
                onResendClick = {
                    isShowDialogResend = true
                    profileEditViewModel.sendEmailVerification(emailState)
                },
                onVerifyClick = {
                    // inviter l'utilisateur a se reconnecter (le déconnecter automatiquement apres success modif)
                    // effacer tous les stacks et naviguer vers splash
                    rootNavController.navigate(Graph.SPLASH) {
                        popUpTo(0) { inclusive = true } // Efface tout le backstack
                        launchSingleTop = true // Évite de recréer la destination si elle est déjà au sommet
                    }
                },
                onDismiss = {
                    // non evoqué pour verification
                }
            )
        }

        if (isShowDialogResend) {
            SimpleButtonDialog(
                emailSendStatus = emailVerificationStatus,
                title = "Lien de confirmation",
                message = when (emailVerificationStatus) {
                    EmailSendStatus.LOADING -> "Envoi en cours..."
                    EmailSendStatus.SENT -> "Envoyé avec succès!"
                    EmailSendStatus.ERROR -> "Erreur d'envoi"
                    EmailSendStatus.IDLE -> "Prêt pour envoye"
                },
                hideButton = true,
                onDismiss = {
                    isShowDialogResend = false
                }
            )
        }

    }

}

// Enum avec des valeurs de type String pour la difficulte et la tranche d'age
enum class ClickEditStatus(val displayed: String) {
    DIFFICULTY("diffuculty"),
    AGERANGE("agerange")
}

enum class ClickCurrentStatus {
    UPDATE,
    DELETE,
    IDLE
}
