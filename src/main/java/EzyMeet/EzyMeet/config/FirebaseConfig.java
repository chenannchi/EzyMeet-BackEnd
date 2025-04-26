package EzyMeet.EzyMeet.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.FirestoreClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.InputStream;

@Configuration
public class FirebaseConfig {
    private static final Logger logger = LoggerFactory.getLogger(FirebaseConfig.class);


    @Bean
    public FirebaseApp firebaseApp() throws Exception {
        // 從 classpath 載入 service account
        InputStream serviceAccount =
                this.getClass().getClassLoader()
                        .getResourceAsStream("firebase/ezymeet-key.json");
        if (serviceAccount == null) {
            logger.error("找不到 firebase key 文件");
            throw new IllegalStateException("找不到 firebase-service-account.json");
        }

        FirebaseOptions options = FirebaseOptions.builder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                // 如果需要 Cloud Storage 或 Realtime DB，可一併設置
                //.setStorageBucket("your-project-id.appspot.com")
                .build();

        // 避免重複初始化

        if (FirebaseApp.getApps().isEmpty()) {
            FirebaseApp.initializeApp(options);
            logger.info("Firebase has been initialized successfully");
        } else {
            logger.info("Firebase is already initialized");
        }
        return FirebaseApp.getInstance();
    }

    @Bean
    public Firestore firestoreClient(FirebaseApp app) {
        // FirestoreClient 其實是 static 單例，但我們依然可以透過 Bean 注入
        logger.info("Firestore client created");
        return FirestoreClient.getFirestore(app);
    }
}