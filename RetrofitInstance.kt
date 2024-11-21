package mg.gasydev.tenymalagasy.data.service.retrofit

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import mg.gasydev.tenymalagasy.data.service.ApiService
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitInstance {

    private suspend fun getIdToken(): String? {
        val firebaseUser = FirebaseAuth.getInstance().currentUser
        return if (firebaseUser != null) {
            try {
                val result = firebaseUser.getIdToken(true).await()  // Utilise await() pour obtenir le résultat dans une coroutine
                result.token
            } catch (e: Exception) {
                null
            }
        } else {
            null
        }
    }

    private val authInterceptor = Interceptor { chain ->
        val originalRequest = chain.request()

        runBlocking {
            val idToken = getIdToken()
            Log.i("DEBUG_CREATE", "TOKEN==> " + idToken)
            val requestWithToken = originalRequest.newBuilder().apply {
                header("Authorization", "Bearer ${idToken ?: ""}")
            }.build()
            chain.proceed(requestWithToken)
        }
    }

    val client = OkHttpClient.Builder()
        .addInterceptor(authInterceptor)
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.MINUTES)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

}