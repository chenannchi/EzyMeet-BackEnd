package EzyMeet.EzyMeet.controller;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.WriteResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
@RestController
@RequestMapping("/firestore-test")
public class FirebaseTestController {

    private final Firestore firestore;
    private static final Logger logger = LoggerFactory.getLogger(FirebaseTestController.class);
    private static final String COLLECTION_NAME = "test_collection";

    @Autowired
    public FirebaseTestController(Firestore firestore) {
        this.firestore = firestore;
    }

    @PostMapping("/create")
    public String createDocument(@RequestParam String docId) {
        try {
            // 準備要存儲的數據
            Map<String, Object> data = new HashMap<>();
            data.put("timestamp", System.currentTimeMillis());
            data.put("message", "測試文檔");
            data.put("created_by", "測試用戶");

            // 執行寫入操作
            WriteResult result = firestore.collection(COLLECTION_NAME)
                    .document(docId)
                    .set(data)
                    .get();  // 同步等待完成

            logger.info("文檔創建成功: {}", result.getUpdateTime());
            return "文檔創建成功！時間戳: " + result.getUpdateTime();
        } catch (Exception e) {
            logger.error("創建文檔失敗", e);
            return "創建失敗: " + e.getMessage();
        }
    }
}