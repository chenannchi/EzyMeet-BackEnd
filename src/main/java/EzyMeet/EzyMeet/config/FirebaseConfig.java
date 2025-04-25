package EzyMeet.EzyMeet.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.FirestoreClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.InputStream;

@Configuration
public class FirebaseConfig {

    @Bean
    public FirebaseApp firebaseApp() throws Exception {
        // 從 classpath 載入 service account
        InputStream serviceAccount =
                this.getClass().getClassLoader()
                        .getResourceAsStream("firebase-service-account.json");
        if (serviceAccount == null) {
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
        }
        return FirebaseApp.getInstance();
    }

    @Bean
    public FirestoreClient firestoreClient(FirebaseApp app) {
        // FirestoreClient 其實是 static 單例，但我們依然可以透過 Bean 注入
        return FirestoreClient.getInstance();
    }
}


//package EzyMeet.EzyMeet.config;
//
//import com.google.auth.oauth2.GoogleCredentials;
//import com.google.cloud.firestore.Firestore;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//import java.io.FileInputStream;
//import java.io.InputStream;
//
//import com.google.firebase.FirebaseApp;
//import com.google.firebase.FirebaseOptions;
//
//@Configuration
//public class FirebaseConfig {
//
//    @Bean
//    public Firestore initializeFirestore() throws Exception {
//        InputStream serviceAccount = new FileInputStream("firebase/ezymeet-key.json");
//        GoogleCredentials credentials = GoogleCredentials.fromStream(serviceAccount);
//        FirebaseOptions options = new FirebaseOptions.Builder()
//                .setCredentials(credentials)
//                .build();
//        FirebaseApp.initializeApp(options);
//
//        return FirestoreClient.getFirestore();
//    }
//}


