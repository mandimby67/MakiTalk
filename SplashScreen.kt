package mg.gasydev.tenymalagasy.presentation.ui.screens.splash

import android.view.animation.OvershootInterpolator
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MarkEmailUnread
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.NavController
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.delay
import mg.gasydev.tenymalagasy.R
import mg.gasydev.tenymalagasy.domain.enumType.EmailSendStatus
import mg.gasydev.tenymalagasy.domain.enumType.UserAgeRange
import mg.gasydev.tenymalagasy.domain.enumType.UserDifficulty
import mg.gasydev.tenymalagasy.presentation.ui.components.group.dialog.EmailNotVerifiedDialog
import mg.gasydev.tenymalagasy.presentation.ui.components.group.dialog.SimpleButtonDialog
import mg.gasydev.tenymalagasy.presentation.ui.navigation.Graph
import mg.gasydev.tenymalagasy.presentation.ui.theme.GreenBackgroundColor
import mg.gasydev.tenymalagasy.presentation.ui.theme.whiteTextColor
import mg.gasydev.tenymalagasy.presentation.viewmodel.service.CountdownViewModel
import mg.gasydev.tenymalagasy.presentation.viewmodel.splash.SplashViewModel

@Composable
fun SplashScreen(
    countdownViewModel: CountdownViewModel,
    navController: NavController,
    splashViewModel: SplashViewModel = hiltViewModel()
) {

    // variable de l'observer de verification ==> auto
    val isEmailCheckable by splashViewModel.isEmailCheckable.collectAsState()
    val isEmailVerified by splashViewModel.isEmailVerified.collectAsState()
    var isEmailNotVerifiedVisible by remember { mutableStateOf(false) }

    // variable du simple verification (sans observer) ===> manuel
    val isUserEmailVerified by splashViewModel.isUserEmailVerified.collectAsState()
    val isUserEmailVerifiedLoading by splashViewModel.isLoading.collectAsState()
    var isShowDialogNotConfirmed by remember { mutableStateOf(false) }

    // variable d'affichage du dialog d'envoi de mail  de verification
    var isShowDialogResend by remember { mutableStateOf(false) }

    // statut de l'envoi du mail de verification pour l'icon sur le dialog (loading si chargement, check si envoyé, ...)
    val emailVerificationStatus by splashViewModel.emailVerificationStatus.collectAsState()

    val animatedScale = remember {
        androidx.compose.animation.core.Animatable(0f)
    }

    LaunchedEffect(isEmailCheckable) {
        if (isEmailCheckable) {
            splashViewModel.observeEmailVerification()
        }
    }

    // Animation
    LaunchedEffect(key1 = true) {

        // Verifier l'etat de l'utilisateur (mail verifié ou non)
        splashViewModel.checkUserEmailState()

        animatedScale.animateTo(
            targetValue = 1.0f,
            animationSpec = tween(
                durationMillis = 900,
                easing = {
                    OvershootInterpolator(4f).getInterpolation(it)
                }
            )
        )
        delay(2000L)

        // pour eviter que le niveau ou la difficulte soit vide lors de l'arrivé dans HOME (VERIFIER CES PREFERENCES)
        if (splashViewModel.isLoggedIn() && splashViewModel.getDifficulty() !== UserDifficulty.IDLE && splashViewModel.getAgeRange() !== UserAgeRange.IDLE) {
            if (isEmailCheckable && !isEmailVerified) {
                isEmailNotVerifiedVisible = true
            } else {
                navController.navigate(Graph.HOME)
            }
        } else {
            navController.navigate(Graph.AUTHENTICATION)
        }

        // seulement pour test => recap
        //navController.navigate("${Graph.RECAP}/1/2")

        // seulement pour test => Test nouveau UI
        //navController.navigate(Graph.TEST)

        // seulement pour test => intro
        //navController.navigate(Graph.INTRO)
    }

    LaunchedEffect(isUserEmailVerified) {
        if (isUserEmailVerified == true)
            navController.navigate(Graph.HOME)
        else if (isUserEmailVerified == false)
            isShowDialogNotConfirmed = true
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxSize()
            .background(GreenBackgroundColor)
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .scale(animatedScale.value),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // Image splash
            Image(
                painter = painterResource(id = R.drawable.logo_splash),
                contentDescription = "Logo",
                modifier = Modifier
                    .size(220.dp)
                    .clip(CircleShape)
            )

            // Texte splash
            Text(
                "MakiTalk",
                style = MaterialTheme.typography.headlineLarge,
                textAlign = TextAlign.Center,
                color = whiteTextColor,
                modifier = Modifier
                    .fillMaxWidth()
            )

        }

        AnimatedVisibility(
            visible = isEmailNotVerifiedVisible,
            enter = fadeIn() + slideInVertically(initialOffsetY = { it / 4 }),
            exit = fadeOut() + slideOutVertically(targetOffsetY = { it / 4 })
        ) {
            EmailNotVerifiedDialog(
                countdownViewModel = countdownViewModel,
                isLoading = isUserEmailVerifiedLoading,
                onResendClick = {
                    isShowDialogResend = true
                    splashViewModel.sendEmailVerification()
                },
                onVerifyClick = {
                    splashViewModel.checkIfUserEmailVerified()
                }
            )
        }

        if (isShowDialogNotConfirmed) {
            SimpleButtonDialog(
                imageVector = Icons.Default.MarkEmailUnread,
                title = "Oups !",
                message = "Email non confirmé.",
                onDismiss = {
                    splashViewModel.resetUserEmailVerified()
                    isShowDialogNotConfirmed = false
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