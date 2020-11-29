package it.toporowicz.infrastructure.quartz

import io.micronaut.context.BeanContext
import org.quartz.Job
import org.quartz.Scheduler
import org.quartz.SchedulerException
import org.quartz.spi.JobFactory
import org.quartz.spi.TriggerFiredBundle
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import javax.inject.Singleton
import kotlin.jvm.Throws


@Singleton
class MicronautJobFactory(private val beanContext: BeanContext) : JobFactory {
    private val log: Logger = LoggerFactory.getLogger(javaClass)

    @Throws(SchedulerException::class)
    override fun newJob(bundle: TriggerFiredBundle, scheduler: Scheduler?): Job {
        val jobDetail = bundle.jobDetail
        val jobClass = jobDetail.jobClass
        return try {
            if (log.isDebugEnabled) {
                log.debug(
                        "Producing instance of Job '" + jobDetail.key +
                                "', class=" + jobClass.name)
            }
            beanContext.getBean(jobClass)
        } catch (e: Exception) {
            val se = SchedulerException(
                    "Problem instantiating class '"
                            + jobDetail.jobClass.name + "'", e)
            throw se
        }
    }

}