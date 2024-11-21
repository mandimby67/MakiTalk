package mg.gasydev.tenymalagasy.presentation.ui.screens.auth

import android.util.Log
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.TweenSpec
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import mg.gasydev.tenymalagasy.R
import mg.gasydev.tenymalagasy.domain.enumType.UserType
import mg.gasydev.tenymalagasy.domain.result.AuthResult
import mg.gasydev.tenymalagasy.presentation.ui.components.button.RoundedButton
import mg.gasydev.tenymalagasy.presentation.ui.components.form.LoginForm
import mg.gasydev.tenymalagasy.presentation.ui.navigation.Graph
import mg.gasydev.tenymalagasy.presentation.ui.theme.DarkGreen
import mg.gasydev.tenymalagasy.presentation.ui.theme.GreenBackNoTransparent
import mg.gasydev.tenymalagasy.presentation.ui.theme.GreenBackgroundColor
import mg.gasydev.tenymalagasy.presentation.viewmodel.auth.AuthViewModel

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun AuthScreen(
    rootNavController: NavController = rememberNavController(),
    authViewModel: AuthViewModel = hiltViewModel()
) {

    val context = LocalContext.current

    // expanded par défaut
    var expandedCard by remember { mutableIntStateOf(authViewModel.expandedCard) }

    // Bouton
    var isLoading by remember { mutableStateOf(false) }

    Surface (color = Color.White) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            // Image placée au-dessus de la colonne
            Image(
                painter = painterResource(id = R.drawable.greeting_auth),
                contentDescription = null,
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(top = 90.dp)
                    .offset(x = (-65).dp)
                    .width(140.dp)
                    .height(IntrinsicSize.Min) // Taille dynamique selon le contenu de la colonne
            )
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 25.dp)
                    .align(Alignment.Center),
                verticalArrangement = Arrangement.Center
            ) {
                ExpandableCard(
                    isExpanded = expandedCard == 0,
                    onCardArrowClick = {
                        // Si la carte n'est pas déjà ouverte, elle se développe
                        if (expandedCard != 0) {
                            expandedCard = 0
                            authViewModel.onCardSelected(0)
                        }
                    },
                    title = context.getString(R.string.auth_compte_connexion)
                ) {
                    LoginForm(
                        rootNavController = rootNavController,
                        formTitle = context.getString(R.string.auth_compte_connexion_desc),
                        authViewModel = authViewModel,
                        onLoginSuccess = {
                            authViewModel.setUserType(UserType.AUNTHENTIFIED)
                            rootNavController.navigate(Graph.HOME)
                        }
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                ExpandableCard(
                    isExpanded = expandedCard == 1,
                    onCardArrowClick = {
                        // Si la carte n'est pas déjà ouverte, elle se développe
                        if (expandedCard != 1) {
                            expandedCard = 1
                            authViewModel.onCardSelected(1)
                        }
                    },
                    title = context.getString(R.string.auth_compte_nouveau)
                ) {

                    val shape = RoundedCornerShape(20.dp)
                    Column(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Box(
                            modifier = Modifier
                            .fillMaxWidth()
                        ) {
                            Box(
                                modifier = Modifier
                                    .background(Color.White, shape)
                                    .height(45.dp)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(start = 2.dp)
                                        .align(Alignment.CenterStart),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(40.dp)
                                            .background(Color.White, shape)
                                            .border(
                                                width = (1.5).dp,
                                                color = GreenBackgroundColor.copy(0.5f),
                                                shape
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Image(
                                            modifier = Modifier.size(15.dp),
                                            painter = painterResource(id = R.drawable.flag_fr),
                                            contentDescription = null,
                                            contentScale = ContentScale.FillHeight
                                        )
                                    }

                                    Text(
                                        modifier = Modifier.padding(horizontal = 7.dp).padding(end = 6.dp),
                                        text = context.getString(R.string.auth_compte_nouveau_paragraphe_1),
                                        style = MaterialTheme.typography.bodySmall,
                                        fontSize = 15.sp,
                                        lineHeight = 18.sp,
                                        color = GreenBackgroundColor,
                                    )

                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                        ) {
                            Box(
                                modifier = Modifier
                                    .background(Color.White, shape)
                                    //.fillMaxWidth()
                                    .height(45.dp)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(start = 2.dp)
                                        .align(Alignment.CenterStart),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(40.dp)
                                            .background(Color.White, shape)
                                            .border(
                                                width = (1.5).dp,
                                                color = GreenBackgroundColor.copy(0.5f),
                                                shape
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Image(
                                            modifier = Modifier.size(15.dp),
                                            painter = painterResource(id = R.drawable.flag_mg),
                                            contentDescription = null,
                                            contentScale = ContentScale.FillHeight
                                        )
                                    }

                                    Text(
                                        modifier = Modifier.padding(horizontal = 7.dp).padding(end = 6.dp),
                                        text = context.getString(R.string.auth_compte_nouveau_paragraphe_2),
                                        style = MaterialTheme.typography.bodySmall,
                                        fontSize = 15.sp,
                                        lineHeight = 18.sp,
                                        color = GreenBackgroundColor,
                                    )

                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(20.dp))
                        Text(
                            modifier = Modifier.padding(start = 7.dp),
                            text = context.getString(R.string.auth_compte_nouveau_desc),
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.bodySmall,
                            fontSize = 15.sp,
                            lineHeight = 20.sp,
                            color = DarkGreen,
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        RoundedButton(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp),
                            onClick = {
                                isLoading = true
                                // Appeler la fonction de connexion
                                authViewModel.signInAnonymously() { result ->
                                    when (result) {
                                        is AuthResult.Success -> {
                                            // Rediriger vers un autre écran
                                            rootNavController.navigate(Graph.INTRO)
                                        }
                                        is AuthResult.Failure -> {
                                            Log.i("DEBUG_AUTH", "Erreur auth anonyme => " + result.error.message)
                                            isLoading = false
                                        }
                                        else -> {
                                            isLoading = false
                                        }
                                    }
                                }
                            },
                            colorNotClicked = GreenBackgroundColor,
                            colorClicked = GreenBackgroundColor,
                            isLoading = isLoading
                        ) {
                            Text(
                                text = context.getString(R.string.auth_compte_nouveau_btn),
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.White
                            )
                        }
                        Spacer(modifier = Modifier.height(9.dp))
                        // A revoir si a mettre ou pas
                        /*Box(
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = context.getString(R.string.auth_compte_nouveau_creer_compte),
                                style = MaterialTheme.typography.displaySmall,
                                textAlign = TextAlign.Center,
                                fontSize = 12.sp,
                                lineHeight = 25.sp,
                                color = Color.Gray,
                                textDecoration = TextDecoration.Underline
                            )
                        }*/
                    }
                }
            }
        }
    }
}

@ExperimentalMaterialApi
@Composable
fun ExpandableCard(
    title: String,
    isExpanded: Boolean,
    onCardArrowClick: () -> Unit,
    padding: Dp = 12.dp,
    content: @Composable () -> Unit
) {
    // Remove the local state management, we rely entirely on `isExpanded`
    val rotationState by animateFloatAsState(
        targetValue = if (isExpanded) 180f else 0f
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize(
                animationSpec = TweenSpec(
                    durationMillis = 300,
                    easing = LinearOutSlowInEasing
                )
            )
        ,
        colors = CardDefaults.cardColors(containerColor = GreenBackNoTransparent),
        shape = RoundedCornerShape(25.dp),
        //onClick = { onCardArrowClick() }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(indication = null, interactionSource = remember { MutableInteractionSource() }) {
                    onCardArrowClick()
                }
                .padding(horizontal = padding, vertical = 5.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    modifier = Modifier
                        .weight(6f)
                        .padding(start = 7.dp)
                        .padding(vertical = 15.dp),
                    text = title,
                    style = MaterialTheme.typography.bodyMedium,
                    fontSize = 17.sp,
                    color = DarkGreen,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                if (!isExpanded) {
                    IconButton(
                        modifier = Modifier
                            .alpha(0.2f)
                            .weight(1f)
                            .rotate(rotationState),
                        onClick = { onCardArrowClick() }, // Handle click
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowDropDown,
                            contentDescription = null,
                            tint = GreenBackgroundColor
                        )
                    }
                }
            }
            if (isExpanded) {
                content()  // Show content only if expanded
            }
        }
    }
}
