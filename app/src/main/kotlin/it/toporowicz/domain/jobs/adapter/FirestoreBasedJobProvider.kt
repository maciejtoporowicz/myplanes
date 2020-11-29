package it.toporowicz.domain.jobs.adapter

import com.google.cloud.firestore.FieldPath
import com.google.cloud.firestore.Firestore
import com.google.cloud.firestore.Query
import com.google.cloud.firestore.QueryDocumentSnapshot
import it.toporowicz.domain.jobs.api.Job
import it.toporowicz.domain.jobs.port.JobProvider

class MissingJobParamException(msg: String) : RuntimeException(msg)

class FirestoreBasedJobProvider(private val firestore: Firestore): JobProvider {
    override fun findAll(): Set<Job> {
        val noFilter: (query: Query) -> Query = { query -> query }
        return query(noFilter)
    }

    override fun findByIds(jobIdsFilter: Set<String>): Set<Job> {
        return query { query -> query.whereIn(FieldPath.documentId(), jobIdsFilter.toList()) }
    }

    private fun query(filter: (query: Query) -> Query): Set<Job> {
        val jobsCollectionSnapshot = firestore
                .collection("jobs")
                .let { filter(it) }
                .get()

        return jobsCollectionSnapshot.get().documents
                .mapNotNull { jobDocument -> toJob(jobDocument) }
                .toSet()
    }

    private fun toJob(jobDocument: QueryDocumentSnapshot): Job {
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