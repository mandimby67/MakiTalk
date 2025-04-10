package mg.gasydev.tenymalagasy.domain.usecase

import mg.gasydev.tenymalagasy.data.repository.ImageBucketRepository
import javax.inject.Inject

class ImageBucketUseCase @Inject constructor(
    private val repository: ImageBucketRepository
) {
    suspend fun execute(): List<String> {
        return repository.fetchImageUrls()
    }
}
