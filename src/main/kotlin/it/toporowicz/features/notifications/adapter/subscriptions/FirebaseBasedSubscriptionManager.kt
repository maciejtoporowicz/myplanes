package it.toporowicz.features.notifications.adapter.subscriptions

import com.google.cloud.firestore.DocumentReference
import com.google.cloud.firestore.DocumentSnapshot
import com.google.cloud.firestore.Firestore
import it.toporowicz.features.notifications.core.subscriptions.SubscriptionData
import it.toporowicz.features.notifications.core.subscriptions.SubscriptionManager
import it.toporowicz.infrastructure.firebase.FirebaseBasedJobConfigProvider

class FirebaseBasedSubscriptionManager(
        private val firestore: Firestore,
        private val firebaseBasedJobConfigProvider: FirebaseBasedJobConfigProvider
) : SubscriptionManager {
    override fun subscribeToNotifications(jobId: String, clientId: String) {
        val clientRef = firestore
                .collection("clients")
                .document(clientId)

        val clientSnapshot = clientRef.get().get()

        if (!clientSnapshot.exists()) {
            throw RuntimeException("Client with id=[${clientId}] does not exist")
        }

        val notificationDocumentRef = firestore.document("/jobs/${jobId}")

        val currentSubscriptions = getSubscriptionRefsFrom(clientSnapshot)

        val newClientData = HashMap(clientSnapshot.data)
                .plus("subscriptions" to currentSubscriptions.plus(notificationDocumentRef))

        clientRef.set(newClientData).get()
    }

    override fun unsubscribeFromNotifications(jobId: String, clientId: String) {
        val clientRef = firestore
                .collection("clients")
                .document(clientId)

        val clientSnapshot = clientRef.get().get()

        if (!clientSnapshot.exists()) {
            throw RuntimeException("Client with id=[${clientId}] does not exist")
        }

        val notificationDocumentRef = firestore.document("/jobs/${jobId}")

        val currentSubscriptions = getSubscriptionRefsFrom(clientSnapshot)

        val newClientData = HashMap(clientSnapshot.data)
                .plus("subscriptions" to currentSubscriptions.filter { sub -> sub.id != notificationDocumentRef.id })

        clientRef.set(newClientData).get()
    }

    override fun getSubscriptionsData(clientId: String): Set<SubscriptionData> {
        val clientRef = firestore
                .collection("clients")
                .document(clientId)

        val clientSnapshot = clientRef.get().get()

        if (!clientSnapshot.exists()) {
            throw RuntimeException("Client with id=[${clientId}] does not exist")
        }

        val currentSubscriptionsIds = getSubscriptionRefsFrom(clientSnapshot).map { it.id }

        return firebaseBasedJobConfigProvider
                .read(jobIds = currentSubscriptionsIds)
                .map { job ->
                    SubscriptionData(
                            job.jobId,
                            job.name,
                            job.coordinates,
                            job.boundaryOffsetNorth,
                            job.boundaryOffsetEast,
                            job.boundaryOffsetSouth,
                            job.boundaryOffsetWest,
                            job.altitudeThreshold
                    )
                }
                .toSet()
    }

    private fun getSubscriptionRefsFrom(clientSnapshot: DocumentSnapshot): Iterable<DocumentReference> {
        return (clientSnapshot.get("subscriptions") as Iterable<*>? ?: return emptySet())
                .map { it as DocumentReference }
    }
}