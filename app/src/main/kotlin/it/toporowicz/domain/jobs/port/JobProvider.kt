package it.toporowicz.domain.jobs.port

import it.toporowicz.domain.jobs.api.Job

interface JobProvider {
    fun findByIds(jobIdsFilter: Set<String>): Set<Job>
    fun findAll(): Set<Job>
}
