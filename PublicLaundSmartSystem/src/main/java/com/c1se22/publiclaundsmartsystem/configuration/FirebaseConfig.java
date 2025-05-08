package com.c1se22.publiclaundsmartsystem.configuration;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

@Configuration
public class FirebaseConfig {
    @Bean
    FirebaseApp firebaseApp(GoogleCredentials credentials) throws IOException {
        FirebaseOptions options = FirebaseOptions.builder()
                .setCredentials(credentials)
                .setProjectId("laundrysystem-d73d7")
                .setDatabaseUrl("https://laundrysystem-d73d7-default-rtdb.asia-southeast1.firebasedatabase.app")
                .build();
        return FirebaseApp.initializeApp(options);
    }
    @Bean
    GoogleCredentials googleCredentials() throws IOException {
        FileInputStream serviceAccount;
        try {
            serviceAccount = new FileInputStream("config/firebase_key.json");
        } catch (FileNotFoundException e) {
            serviceAccount = new FileInputStream("src/main/resources/config/firebase_key.json");
        }
        return GoogleCredentials.fromStream(serviceAccount);
    }
    @Bean
    FirebaseMessaging firebaseMessaging(FirebaseApp firebaseApp) throws IOException {
        return FirebaseMessaging.getInstance(firebaseApp);
    }

    @Bean
    public FirebaseDatabase firebaseDatabase(FirebaseApp firebaseApp) throws IOException {
        return FirebaseDatabase.getInstance(firebaseApp);
    }
}
