package mg.gasydev.tenymalagasy.data.service

import mg.gasydev.tenymalagasy.data.model.api.request.AuthUserRequest
import mg.gasydev.tenymalagasy.data.model.api.response.AllCategories
import mg.gasydev.tenymalagasy.data.model.api.response.AuthUser
import mg.gasydev.tenymalagasy.data.model.api.response.Quizz
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {

    @GET("quizz")
    suspend fun getQuizz(@Query("category") categorie: String, @Query("difficulty") difficulte: String,  @Query("subcategory") souscategory: String): Response<Quizz>

    @GET("allcategories")
    suspend fun getAllCategories(@Query("difficulty") difficulte: String): Response<AllCategories>

    @POST("users")
    suspend fun createUser(@Body userRequest: AuthUserRequest): Response<AuthUser>

    @GET("users/{uid}")
    suspend fun getUserById(@Path("uid") uid: String): Response<AuthUser>

    @DELETE("users/{uid}")
    suspend fun deleteUser(@Path("uid") uid: String): Response<Unit>

}