package mg.gasydev.tenymalagasy.data.repository

import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class ImageBucketRepository @Inject constructor() {
    private val storage = FirebaseStorage.getInstance().reference

    suspend fun fetchImageUrls(): List<String> {
        //val imagesRef = storage.child("images/avatars") // vrai dossier
        val imagesRef = storage.child("images/avatars") // test dossier
        val urls = mutableListOf<String>()

        val result = imagesRef.listAll().await()
        for (item in result.items) {
            val url = item.downloadUrl.await().toString()
            urls.add(url)
        }

        return urls
    }
}
