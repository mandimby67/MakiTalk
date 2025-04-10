package mg.gasydev.tenymalagasy.presentation.ui.components.sheet

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Password
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import mg.gasydev.tenymalagasy.R
import mg.gasydev.tenymalagasy.domain.enumType.InscriptionFieldType
import mg.gasydev.tenymalagasy.presentation.ui.components.button.RoundedButton
import mg.gasydev.tenymalagasy.presentation.ui.components.textfield.CustomTextField
import mg.gasydev.tenymalagasy.presentation.ui.theme.GreenBackNoTransparent
import mg.gasydev.tenymalagasy.presentation.ui.theme.GreenBackgroundColor
import mg.gasydev.tenymalagasy.presentation.ui.theme.GreenBottomSheet

@Composable
fun PasswordBottomSheet(
    modifier: Modifier = Modifier,
    isVisible: Boolean,
    isLoading: Boolean,
    onValidateRequest: (password: String) -> Unit
) {

    val context = LocalContext.current
    var isButtonValidateEnabled by remember { mutableStateOf(false) }
    val bottomSheetHeight = 220.dp
    val offsetAnimation by animateDpAsState(
        targetValue = if (isVisible) 0.dp else bottomSheetHeight,
        animationSpec = tween(durationMillis = 500, easing = FastOutSlowInEasing)
    )

    // state
    var passwordState by remember { mutableStateOf("") }

    LaunchedEffect(isVisible) {
        if (!isVisible) passwordState = ""
    }

    LaunchedEffect(passwordState) {
        isButtonValidateEnabled = passwordState.isNotEmpty()
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(bottomSheetHeight)
            .offset(y = offsetAnimation)
            .background(
                GreenBottomSheet,
                RoundedCornerShape(topStart = 22.dp, topEnd = 16.dp)
            )
    ) {
        // Contenu du BottomSheet
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 25.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                textAlign = TextAlign.Left,
                text = context.getString(R.string.profile_edit_saisie_mdp),
                color = Color.White,
                style = MaterialTheme.typography.bodyMedium,
                lineHeight = 17.sp,
                fontSize = 14.sp,
            )
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
            Spacer(modifier = Modifier.height(16.dp))
            RoundedButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                onClick = {
                    onValidateRequest(passwordState)
                },
                colorNotClicked = if (isButtonValidateEnabled) GreenBackgroundColor else GreenBackgroundColor.copy(
                    0.6f
                ),
                colorClicked = GreenBackgroundColor,
                enabled = isButtonValidateEnabled,
                isLoading = isLoading
            ) {
                Text(
                    text = context.getString(R.string.profile_champ_validation),
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White
                )
            }
        }
    }

}
