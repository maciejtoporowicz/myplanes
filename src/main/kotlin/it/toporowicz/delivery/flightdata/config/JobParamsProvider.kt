package it.toporowicz.delivery.flightdata.config

import com.google.cloud.firestore.Firestore
import com.google.cloud.firestore.QueryDocumentSnapshot
import org.slf4j.LoggerFactory
import javax.inject.Singleton

class MissingJobParamException(msg: String) : RuntimeException(msg)

@Singleton
class JobParamsProvider(private val firestore: Firestore) {
    companion object {
        private val log = LoggerFactory.getLogger(JobParamsProvider::class.java)
    }

    fun read(): Set<JobParams> {
        val jobsCollectionSnapshot = firestore.collection("jobs").get()

        return jobsCollectionSnapshot.get().documents
                .mapNotNull { jobDocument -> silentReadFrom(jobDocument) }
                .toSet()
    }

    private fun silentReadFrom(jobDocument: QueryDocumentSnapshot): JobParams? {
        return try {
            JobParams(
                    jobId = jobDocument.id,
                    name = jobDocument.getString("name") ?: throw MissingJobParamException("name"),
                    coordinates = jobDocument.getString("coordinates") ?: throw MissingJobParamException("coordinates"),
                    boundaryOffsetNorth =jobDocument.getLong("boundaryOffsetNorth")?.toString() ?: throw MissingJobParamException("boundaryOffsetNorth"),
                    boundaryOffsetEast =jobDocument.getLong("boundaryOffsetEast")?.toString() ?: throw MissingJobParamException("boundaryOffsetEast"),
                    boundaryOffsetSouth =jobDocument.getLong("boundaryOffsetSouth")?.toString() ?: throw MissingJobParamException("boundaryOffsetSouth"),
                    boundaryOffsetWest =jobDocument.getLong("boundaryOffsetWest")?.toString() ?: throw MissingJobParamException("boundaryOffsetWest"),
                    altitudeThreshold = jobDocument.getLong("altitudeThreshold")?.toString() ?: throw MissingJobParamException("altitudeThreshold"),
            )
        } catch (ex: RuntimeException) {
            log.info("Could not parse job with id = ${jobDocument.id}", ex)
            null
        }
    }
}