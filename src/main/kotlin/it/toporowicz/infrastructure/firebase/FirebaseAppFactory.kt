package it.toporowicz.infrastructure.firebase

import com.google.auth.oauth2.GoogleCredentials
import com.google.cloud.firestore.Firestore
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.cloud.FirestoreClient
import com.google.firebase.messaging.FirebaseMessaging
import io.micronaut.context.annotation.ConfigurationProperties
import io.micronaut.context.annotation.Factory
import org.jetbrains.annotations.NotNull
import java.io.FileInputStream
import javax.inject.Singleton

@ConfigurationProperties("google.config")
interface GoogleConfig {
    @get:NotNull
    val path: String

    @get:NotNull
    val databaseUrl: String
}

@Factory
class FirebaseAppFactory {
    @Singleton
    fun create(config: GoogleConfig): FirebaseApp {
        val serviceAccount = FileInputStream(config.path)

        val options = FirebaseOptions.builder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                .setDatabaseUrl(config.databaseUrl)
                .build()

        return FirebaseApp.initializeApp(options)
    }

    @Singleton
    fun createFirestore(firebaseApp: FirebaseApp): Firestore {
        return FirestoreClient.getFirestore(firebaseApp)
    }

    @Singleton
    fun createMessaging(firebaseApp: FirebaseApp): FirebaseMessaging {
        return FirebaseMessaging.getInstance(firebaseApp)
    }
}