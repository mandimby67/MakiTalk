package mg.gasydev.tenymalagasy.presentation.ui.components.group.dialog

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import mg.gasydev.tenymalagasy.R
import mg.gasydev.tenymalagasy.domain.enumType.EmailSendStatus
import mg.gasydev.tenymalagasy.presentation.ui.components.progressbar.SendingCircularProgress
import mg.gasydev.tenymalagasy.presentation.ui.theme.GreenBackgroundColor

@Composable
fun SimpleButtonDialog(
    modifier: Modifier = Modifier,
    imageVector: ImageVector? = null,
    animateImageVector: Boolean = false,
    title: String = "",
    message: String = "",
    hideButton: Boolean = false,
    hideClose: Boolean = false,
    emailSendStatus: EmailSendStatus? = null,
    onDismiss: () -> Unit = {},
    hasAction: Boolean = false,
    onAction: () -> Unit = {},
) {

    val context = LocalContext.current
    val spacerWeight = 1f

    // Animation de la rotation pour imageVector
    val infiniteTransition = rememberInfiniteTransition()
    val rotationAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1000, easing = LinearEasing), // Animation fluide
            repeatMode = RepeatMode.Restart
        ), label = "inifiniteRotationImageVector"
    )

    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = modifier
                .size(240.dp)
                //.heightIn(min = 240.dp)
                .background(Color.White, shape = RoundedCornerShape(16.dp))
        ) {

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White, shape = RoundedCornerShape(16.dp))
                    .padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                if (emailSendStatus != null) {
                    Spacer(modifier = Modifier.weight(spacerWeight))
                    SendingCircularProgress(status = emailSendStatus)
                }

                if (imageVector != null) {
                    Spacer(modifier = Modifier.weight(spacerWeight))
                    Icon(
                        modifier = Modifier
                            .size(50.dp)
                            .then(
                                if (animateImageVector) {
                                    Modifier.graphicsLayer(rotationZ = rotationAngle) // Rotation appliquée si `animateImageVector` est `true`
                                } else {
                                    Modifier // Pas d'animation si `animateImageVector` est `false`
                                }
                            ),
                        imageVector = imageVector,
                        contentDescription = null,
                        tint = GreenBackgroundColor.copy(0.5f)
                    )
                }

                if (title.isNotEmpty()) {
                    Spacer(modifier = Modifier.weight(spacerWeight))
                    Text(
                        text = title,
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.bodyMedium,
                        fontSize = 18.sp,
                        color = GreenBackgroundColor,
                    )
                }

                if (message.isNotEmpty()) {
                    Spacer(modifier = Modifier.weight(spacerWeight))
                    Text(
                        text = message,
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.bodyMedium,
                        fontSize = 15.sp,
                        color = Color.Gray,
                    )
                }
                Spacer(modifier = Modifier.weight(spacerWeight))
                if (!hideButton) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Button(
                            onClick = {
                                if (hasAction) {
                                    onAction()
                                    onDismiss()
                                } else {
                                    onDismiss()
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = GreenBackgroundColor),
                            shape = RoundedCornerShape(25),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = context.getString(R.string.lesson_dialog_ok),
                                style = MaterialTheme.typography.bodyMedium,
                                fontSize = 14.sp,
                                color = Color.White
                            )
                        }

                    }
                }
                Spacer(modifier = Modifier.weight(spacerWeight))
            }

            if(!hideClose) {
                Box(
                    modifier = Modifier
                        .padding(top = 8.dp, end = 13.dp)
                        .size(40.dp)
                        .clip(CircleShape)
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

}
