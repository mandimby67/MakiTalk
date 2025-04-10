package mg.gasydev.tenymalagasy.presentation.ui.components.group.image

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.LightGray
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import mg.gasydev.tenymalagasy.R
import mg.gasydev.tenymalagasy.presentation.ui.components.button.RoundedButton
import mg.gasydev.tenymalagasy.presentation.ui.theme.GreenBackNoTransparent
import mg.gasydev.tenymalagasy.presentation.ui.theme.GreenBackgroundColor

@Composable
fun ProfileAvatarSelect(
    imageUrls: List<String>,
    onImageSelected: (url: String) -> Unit,
    onValidateSelection: (selectedImageUrl: String?) -> Unit,
    onDismiss: () -> Unit,
    selectedImageUrl: String?,
    isLoading: Boolean,
    isImagesError: Boolean
) {

    // context
    val context = LocalContext.current

    Box (
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .clickable(
                onClick = {},  // Pour gérer le clic sur l'overlay
                indication = null,  // Désactive les animations d'interaction
                interactionSource = remember { MutableInteractionSource() }
            )
    ) {
        if (isImagesError) {
            Text(
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(horizontal = 25.dp),
                text = context.getString(R.string.profile_edit_error_image),
                style = MaterialTheme.typography.bodyLarge,
                color = Color.Gray
            )
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                Spacer(modifier = Modifier.height(60.dp))
                Text(
                    text = context.getString(R.string.profile_edit_selection_image),
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.Gray,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                if (isLoading) {
                    // Affiche un squelette de chargement pendant le chargement des images
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(4),
                        modifier = Modifier.weight(1f)
                    ) {
                        items(12) { index ->
                            val alphaAnimation by rememberInfiniteTransition(label = "alphaCircle$index").animateFloat(
                                initialValue = 0.6f,
                                targetValue = 1f,
                                animationSpec = infiniteRepeatable(
                                    animation = tween(700 + (index * 250), easing = FastOutSlowInEasing),
                                    repeatMode = RepeatMode.Reverse
                                ), label = "specCircle$index"
                            )
                            Box(
                                modifier = Modifier
                                    .padding(8.dp)
                                    .size(70.dp)
                                    .clip(CircleShape)
                                    .background(LightGray.copy(alpha = alphaAnimation)) // Squelette
                            )
                        }
                    }
                } else {
                    // Affiche la grille d'images si elles sont chargées
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(4),
                        modifier = Modifier.weight(1f),
                        // contentPadding = PaddingValues(vertical = 36.dp)
                    ) {
                        items(imageUrls) { url ->
                            val borderColor =
                                if (url == selectedImageUrl) GreenBackgroundColor else Color.Transparent

                            val backGroundColor =
                                if (url == selectedImageUrl) GreenBackgroundColor else GreenBackNoTransparent.copy(alpha = 0.7f)

                            Box(
                                modifier = Modifier
                                    .padding(8.dp)
                                    .size(70.dp)
                                    .clip(CircleShape) // Appliquer la découpe circulaire ici pour le conteneur
                                    .border(
                                        2.dp,
                                        borderColor,
                                        CircleShape
                                    ) // Appliquer la bordure circulaire
                                    .background(backGroundColor) // Couleur de fond pour le squelette de chargement
                                    .clickable { onImageSelected(url) }
                            ) {
                                AsyncImage(
                                    model = url,
                                    contentDescription = null,
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(6.dp)
                                        .clip(CircleShape), // Découpe l'image elle-même en cercle
                                    placeholder = painterResource(id = R.drawable.image_placeholder), // Image de chargement
                                    error = painterResource(id = R.drawable.image_placeholder),
                                    contentScale = ContentScale.Fit // Pour que l'image remplisse le cercle
                                )
                            }
                        }
                    }

                }

                RoundedButton(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    onClick = {
                        onValidateSelection(selectedImageUrl)
                    },
                    colorNotClicked = if (selectedImageUrl != null) GreenBackgroundColor else GreenBackgroundColor.copy(
                        0.4f
                    ),
                    colorClicked = GreenBackgroundColor,
                    enabled = selectedImageUrl != null,
                ) {
                    Text(
                        text = context.getString(R.string.profile_edit_selection_confirmation),
                        style = MaterialTheme.typography.bodyMedium,
                        fontSize = 15.sp,
                        color = Color.White
                    )
                }
            }
        }

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
