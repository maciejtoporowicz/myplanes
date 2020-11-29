package it.toporowicz.domain.jobs.core

import it.toporowicz.domain.jobs.api.Job
import it.toporowicz.domain.jobs.port.JobProvider

class JobsModule(private val jobProvider: JobProvider) {
    fun findAllJobs(): Set<Job> {
        return jobProvider.findAll()
    }

    fun findJobsByIds(jobsIds: Set<String>): Set<Job> {
        return jobProvider.findByIds(jobsIds)
    }
}