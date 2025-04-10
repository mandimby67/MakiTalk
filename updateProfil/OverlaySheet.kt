package mg.gasydev.tenymalagasy.presentation.ui.components.sheet

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color


@Composable
fun OverlaySheet(
    isVisible: Boolean,
    overlayColor: Color = Color.Black.copy(alpha = 0.5f),
    onDismissRequest: () -> Unit
) {
    AnimatedVisibility(
        visible = isVisible,
        enter = fadeIn(animationSpec = tween(durationMillis = 600)),
        exit = fadeOut(animationSpec = tween(durationMillis = 600))
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(overlayColor) // Fond semi-transparent par defaut
                .clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() }
                ) {
                    onDismissRequest() // Fermer l'overlay lors du clic
                }
        )
    }
}
