package it.toporowicz.domain.jobs

import com.google.cloud.firestore.Firestore
import io.micronaut.context.annotation.Factory
import it.toporowicz.domain.jobs.adapter.FirestoreBasedJobProvider
import it.toporowicz.domain.jobs.core.JobsModule
import javax.inject.Singleton

@Factory
class JobProviderFactory {
    @Singleton
    fun create(firestore: Firestore): JobsModule {
        return JobsModule(FirestoreBasedJobProvider(firestore))
    }
}