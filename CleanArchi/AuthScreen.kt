@Composable
fun AuthScreen(
    viewModel: AuthViewModel = hiltViewModel()
) {
    val authState by viewModel.authState.observeAsState(AuthResult.Idle)
    val isLoading by viewModel.loading.observeAsState(false)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        when (authState) {
            is AuthResult.Idle -> Text(text = "Bienvenue, veuillez vous connecter.")
            is AuthResult.Success -> {
                val user = (authState as AuthResult.Success<FirebaseUser?>).data
                Text(text = "Connecté en tant que ${user?.uid ?: "Anonyme"}")
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = { viewModel.signOut() }) {
                    Text(text = "Déconnexion")
                }
            }
            is AuthResult.Failure -> {
                val error = (authState as AuthResult.Failure).error.message ?: "Erreur inconnue"
                Text(text = "Erreur : $error", color = Color.Red)
            }
            AuthResult.InvalidCredentials -> Text(text = "Identifiants invalides.", color = Color.Red)
            AuthResult.NetworkException -> Text(text = "Problème réseau.", color = Color.Red)
            else -> Unit
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (isLoading) {
            CircularProgressIndicator()
        } else {
            Button(onClick = { viewModel.signInAnonymously() }) {
                Text(text = "Connexion anonyme")
            }
            Spacer(modifier = Modifier.height(8.dp))
            Button(onClick = {
                // Remplacez par vos valeurs d'email/mot de passe pour test
                viewModel.signInWithEmailAndPassword("test@example.com", "password123")
            }) {
                Text(text = "Connexion avec email")
            }
        }
    }
}
