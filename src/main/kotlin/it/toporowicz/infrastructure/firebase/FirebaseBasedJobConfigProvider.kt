package it.toporowicz.infrastructure.firebase

import com.google.cloud.firestore.FieldPath
import com.google.cloud.firestore.Firestore
import com.google.cloud.firestore.QueryDocumentSnapshot
import org.slf4j.LoggerFactory
import javax.inject.Singleton

class MissingJobParamException(msg: String) : RuntimeException(msg)

@Singleton
class FirebaseBasedJobConfigProvider(private val firestore: Firestore) {
    companion object {
        private val log = LoggerFactory.getLogger(FirebaseBasedJobConfigProvider::class.java)
    }

    fun read(silent: Boolean = false, jobIds: List<String> = emptyList()): Set<Job> {
        val jobsCollectionSnapshot = firestore.collection("jobs").whereIn(FieldPath.documentId(), jobIds).get()

        return jobsCollectionSnapshot.get().documents
                .mapNotNull { jobDocument -> if (silent) silentReadFrom(jobDocument) else readFrom(jobDocument) }
                .toSet()
    }

    private fun silentReadFrom(jobDocument: QueryDocumentSnapshot): Job? {
        return try {
            readFrom(jobDocument)
        } catch (ex: RuntimeException) {
            log.info("Could not read job", ex)
            null
        }
    }

    private fun readFrom(jobDocument: QueryDocumentSnapshot): Job {
        return Job(
                jobId = jobDocument.id,
                name = jobDocument.getString("name") ?: throw MissingJobParamException("name"),
                coordinates = jobDocument.getString("coordinates") ?: throw MissingJobParamException("coordinates"),
                boundaryOffsetNorth = jobDocument.getLong("boundaryOffsetNorth")?.toString()
                        ?: throw MissingJobParamException("boundaryOffsetNorth"),
                boundaryOffsetEast = jobDocument.getLong("boundaryOffsetEast")?.toString()
                        ?: throw MissingJobParamException("boundaryOffsetEast"),
                boundaryOffsetSouth = jobDocument.getLong("boundaryOffsetSouth")?.toString()
                        ?: throw MissingJobParamException("boundaryOffsetSouth"),
                boundaryOffsetWest = jobDocument.getLong("boundaryOffsetWest")?.toString()
                        ?: throw MissingJobParamException("boundaryOffsetWest"),
                altitudeThreshold = jobDocument.getLong("altitudeThreshold")?.toString()
                        ?: throw MissingJobParamException("altitudeThreshold"),
        )
    }
}