package it.toporowicz.domain.subscriptions.adapter

import com.google.cloud.firestore.DocumentReference
import com.google.cloud.firestore.DocumentSnapshot
import com.google.cloud.firestore.Firestore
import it.toporowicz.domain.subscriptions.port.Subscription
import it.toporowicz.domain.subscriptions.port.SubscriptionRepo

class FirebaseBasedSubscriptionRepo(
        private val firestore: Firestore
): SubscriptionRepo {
    override fun save(subscription: Subscription) {
        val clientRef = firestore
                .collection("clients")
                .document(subscription.clientId)

        val clientSnapshot = clientRef.get().get()

        if (!clientSnapshot.exists()) {
            throw RuntimeException("Client with id=[${subscription.clientId}] does not exist")
        }

        val notificationDocumentRef = firestore.document("/jobs/${subscription.jobId}")

        val currentSubscriptions = getSubscriptionRefsFrom(clientSnapshot)

        val newClientData = HashMap(clientSnapshot.data)
                .plus("subscriptions" to currentSubscriptions.plus(notificationDocumentRef))

        clientRef.set(newClientData).get()
    }

    override fun remove(subscription: Subscription) {
        val clientRef = firestore
                .collection("clients")
                .document(subscription.clientId)

        val clientSnapshot = clientRef.get().get()

        if (!clientSnapshot.exists()) {
            throw RuntimeException("Client with id=[${subscription.clientId}] does not exist")
        }

        val notificationDocumentRef = firestore.document("/jobs/${subscription.jobId}")

        val currentSubscriptions = getSubscriptionRefsFrom(clientSnapshot)

        val newClientData = HashMap(clientSnapshot.data)
                .plus("subscriptions" to currentSubscriptions.filter { sub -> sub.id != notificationDocumentRef.id })

        clientRef.set(newClientData).get()
    }

    override fun getIdsOfJobsToWhichClientIsSubscribed(clientId: String): Set<String> {
        val clientRef = firestore
                .collection("clients")
                .document(clientId)

        val clientSnapshot = clientRef.get().get()

        if (!clientSnapshot.exists()) {
            throw RuntimeException("Client with id=[${clientId}] does not exist")
        }

        return getSubscriptionRefsFrom(clientSnapshot).map { it.id }.toSet()
    }

    private fun getSubscriptionRefsFrom(clientSnapshot: DocumentSnapshot): Iterable<DocumentReference> {
        return (clientSnapshot.get("subscriptions") as Iterable<*>? ?: return emptySet())
                .map { it as DocumentReference }
    }
}