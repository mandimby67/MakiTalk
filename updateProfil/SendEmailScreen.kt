package mg.gasydev.tenymalagasy.presentation.ui.screens.auth

import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import mg.gasydev.tenymalagasy.R
import mg.gasydev.tenymalagasy.domain.enumType.EmailSendType
import mg.gasydev.tenymalagasy.presentation.ui.components.button.RoundedButton
import mg.gasydev.tenymalagasy.presentation.ui.theme.DarkGreen
import mg.gasydev.tenymalagasy.presentation.ui.theme.GreenBackgroundColor
import mg.gasydev.tenymalagasy.presentation.viewmodel.service.CountdownViewModel
import java.util.concurrent.TimeUnit

@Composable
fun SendEmailScreen(
    countdownViewModel: CountdownViewModel,
    isLoading: Boolean = false,
    sendType: EmailSendType,
    onResendClick: (sendType: EmailSendType) -> Unit,
    onVerifyClick: () -> Unit,
    onDismiss: () -> Unit
) {

    // Variable bouton de renvoi
    val context = LocalContext.current
    val isButtonResendEnabled by countdownViewModel.isButtonResendEnabled.collectAsState()
    val timeRemaining by countdownViewModel.timeRemainingFlow.collectAsState()
    val minutes = TimeUnit.MILLISECONDS.toMinutes(timeRemaining).toInt()
    val seconds = (TimeUnit.MILLISECONDS.toSeconds(timeRemaining) % 60).toInt()

    // Gère le comportement du bouton de retour pour le bloquer
    BackHandler(enabled = sendType == EmailSendType.VERIFICATION) {

    }

    LaunchedEffect(Unit) {
        Log.i("DEBUG_TIME", "timeRemainingFlow ==> " + timeRemaining)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .clickable(enabled = false) {}, // Pour bloquer les interactions à travers
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = 25.dp)
                    .padding(bottom = 70.dp),
                contentAlignment = Alignment.BottomCenter
            ) {
                Image(
                    modifier = Modifier.height(170.dp),
                    painter = painterResource(id = R.drawable.letter_box),
                    contentScale = ContentScale.FillHeight,
                    contentDescription = null
                )
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = 25.dp),
                contentAlignment = Alignment.TopCenter
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {

                    val textIntro = when (sendType) {
                        EmailSendType.VERIFICATION -> context.getString(R.string.send_email_intro_verification)
                        EmailSendType.REINITIALISATION -> context.getString(R.string.send_email_intro_reinitialisation)
                        EmailSendType.VERIFICATION_MODIFICATION -> context.getString(R.string.send_email_intro_verification_modification)
                    }
                    Text(
                        text = textIntro,
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Center,
                        fontSize = 22.sp,
                        color = GreenBackgroundColor,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    val textInstruction = when (sendType) {
                        EmailSendType.VERIFICATION -> context.getString(R.string.send_email_instruction_verification)
                        EmailSendType.REINITIALISATION -> context.getString(R.string.send_email_instruction_reinitialisation)
                        EmailSendType.VERIFICATION_MODIFICATION -> context.getString(R.string.send_email_instruction_verification_modification)
                    }
                    Text(
                        text = textInstruction,
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center,
                        fontSize = 15.sp,
                        color = Color.Gray
                    )
                    Spacer(modifier = Modifier.height(30.dp))
                    val textNotReceived = if (sendType == EmailSendType.VERIFICATION || sendType == EmailSendType.VERIFICATION_MODIFICATION) context.getString(R.string.send_email_not_received_verification) else context.getString(R.string.send_email_not_received_reinitialisation)
                    Text(
                        text = textNotReceived,
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center,
                        fontSize = 15.sp,
                        color = DarkGreen
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    // Bouton de renvoie de mail
                    Button(
                        onClick = {
                            Log.i("DEBUG_CLICKED", "onResendClick")
                            onResendClick(sendType)
                            countdownViewModel.startService()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                        shape = RoundedCornerShape(22.dp),
                        border = BorderStroke(
                            1.dp,
                            GreenBackgroundColor
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        enabled = isButtonResendEnabled
                    ) {
                        Text(
                            text = context.getString(R.string.send_email_btn_renvoyer),
                            style = MaterialTheme.typography.bodyMedium,
                            fontSize = 16.sp,
                            color = if (isButtonResendEnabled) GreenBackgroundColor else Color.Gray.copy(0.4f)
                        )
                        if (!isButtonResendEnabled) {
                            Spacer(modifier = Modifier.width(16.dp))
                            Box(
                                modifier = Modifier
                                    .height(27.dp)
                                    .background(
                                        Color.Gray.copy(0.25f),
                                        RoundedCornerShape(9.dp)
                                    )
                                    .padding(horizontal = 9.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = String.format("%02d:%02d", minutes, seconds),
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontSize = 13.sp,
                                    color = Color.White,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }

                    if (sendType == EmailSendType.VERIFICATION || sendType == EmailSendType.VERIFICATION_MODIFICATION) {
                        Spacer(modifier = Modifier.height(16.dp))
                        // Bouton retour
                        RoundedButton(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp),
                            onClick = {
                                onVerifyClick()
                            },
                            colorNotClicked = GreenBackgroundColor,
                            colorClicked = GreenBackgroundColor,
                            isLoading = isLoading,
                        ) {
                            val textButton = if (sendType == EmailSendType.VERIFICATION) context.getString(R.string.send_email_btn_cest_parti) else context.getString(R.string.send_email_btn_se_reconnecter)
                            Text(
                                text = textButton,
                                style = MaterialTheme.typography.bodyMedium,
                                fontSize = 16.sp,
                                color = Color.White
                            )
                        }
                    } else {
                        Spacer(modifier = Modifier.weight(1f))
                        Text(
                            modifier = Modifier.align(Alignment.CenterHorizontally),
                            textAlign = TextAlign.Center,
                            text = context.getString(R.string.send_email_question_reinitialisation),
                            color = Color.Gray,
                            style = MaterialTheme.typography.bodyMedium,
                            fontSize = 15.sp
                        )
                        Spacer(modifier = Modifier.height(3.dp))
                        Text(
                            modifier = Modifier
                                .align(Alignment.CenterHorizontally)
                                .clickable(
                                    indication = null,
                                    interactionSource = remember { MutableInteractionSource() }) {
                                    onDismiss()
                                },
                            textAlign = TextAlign.Center,
                            text = context.getString(R.string.send_email_incitation_connexion),
                            color = GreenBackgroundColor,
                            style = MaterialTheme.typography.bodyMedium,
                            fontSize = 15.sp,
                            textDecoration = TextDecoration.Underline
                        )
                        Spacer(modifier = Modifier.height(40.dp))
                    }

                }
            }
        }

        if (sendType == EmailSendType.REINITIALISATION) {
            Box(
                modifier = Modifier
                    .padding(top = 20.dp, end = 20.dp)
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(GreenBackgroundColor.copy(0.2f), CircleShape)
                    .align(Alignment.TopEnd)
                    .clickable {
                        onDismiss()
                    },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    modifier = Modifier.size(25.dp),
                    imageVector = Icons.Default.Close,
                    contentDescription = null,
                    tint = GreenBackgroundColor
                )
            }
        }

    }
}
