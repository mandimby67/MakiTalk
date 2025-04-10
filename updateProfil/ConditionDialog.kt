package mg.gasydev.tenymalagasy.presentation.ui.components.group.dialog

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import mg.gasydev.tenymalagasy.R
import mg.gasydev.tenymalagasy.presentation.ui.components.link.LinkText
import mg.gasydev.tenymalagasy.presentation.ui.theme.GreenBackgroundColor
import mg.gasydev.tenymalagasy.presentation.ui.theme.disabledContentColor
import mg.gasydev.tenymalagasy.utils.environment.EnvironmentConfig

@Composable
fun ConditionDialog(
    showDialog: Boolean,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    message: String,
    imageDrawable: Int = -1,
    isCgu: Boolean = true, // dialogue initialement concu pour acceptation cgu (aussi utilisé sur suppression de compte ==> false)
    imageVector: ImageVector? = null
) {
    val context = LocalContext.current
    val baseUrlFront = EnvironmentConfig.getBaseUrlFront()

    if (showDialog) {
        Dialog(onDismissRequest = onDismiss) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    //.padding(horizontal = 10.dp)
                    .heightIn(min = 270.dp)
                    .background(Color.White, shape = RoundedCornerShape(16.dp))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White, shape = RoundedCornerShape(16.dp))
                        .padding(horizontal = 24.dp, vertical = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Icône en haut
                    if (imageDrawable != -1) {
                        Icon(
                            painter = painterResource(id = imageDrawable),
                            contentDescription = null,
                            modifier = Modifier.size(50.dp),
                            tint = GreenBackgroundColor
                        )
                    }
                    if (imageVector != null) {
                        Icon(
                            modifier = Modifier.size(50.dp),
                            imageVector = imageVector,
                            contentDescription = null,
                            tint = GreenBackgroundColor
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Texte de confirmation
                    Text(
                        text = message,
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.bodyMedium,
                        fontSize = 15.sp,
                        color = disabledContentColor(),
                    )

                    if (isCgu) {
                        Spacer(modifier = Modifier.height(12.dp))

                        // Liens
                        Column(
                            //verticalArrangement = Arrangement.spacedBy(4.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            LinkText(
                                text = context.getString(R.string.setting_cgu),
                                url = baseUrlFront + "cgu",
                                isCenter = true
                            )
                            LinkText(
                                text = context.getString(R.string.setting_politique),
                                url = baseUrlFront + "confidentialite",
                                isCenter = true
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Boutons "Accepter" et "Refuser"
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {

                        Button(
                            onClick = {
                                onConfirm()
                                onDismiss()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = GreenBackgroundColor),
                            shape = RoundedCornerShape(25),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = if (isCgu) context.getString(R.string.profil_cgu_accept) else "OUI",
                                style = MaterialTheme.typography.bodyMedium,
                                fontSize = 14.sp,
                                color = Color.White
                            )
                        }

                        Button(
                            onClick = {
                                onDismiss()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                            shape = RoundedCornerShape(25),
                            border = BorderStroke(1.dp, GreenBackgroundColor),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = if (isCgu) context.getString(R.string.profil_cgu_reject) else "NON",
                                style = MaterialTheme.typography.bodyMedium,
                                fontSize = 14.sp,
                                color = GreenBackgroundColor
                            )
                        }
                    }
                }
            }
        }
    }
}

