package mg.gasydev.tenymalagasy.data.repository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import mg.gasydev.tenymalagasy.data.database.AppDatabase
import javax.inject.Inject

class DatabaseRepository @Inject constructor(private val appDatabase: AppDatabase) {

    suspend fun clearDatabase() {
        withContext(Dispatchers.IO) {
            appDatabase.clearAllTables()
        }
    }
}
