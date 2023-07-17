package net.saganetwork.sagaproxy.detectors;

import java.util.HashMap;
import java.util.Map;

public class BotDetection {
    private static final int MAX_REQUESTS_PER_SECOND = 10; // İzin verilen maksimum istek hızı

    private Map<String, Long> requestCountMap = new HashMap<>();

    public boolean isBot(String ipAddress) {
        long currentTimeMillis = System.currentTimeMillis();
        long requestCount = requestCountMap.getOrDefault(ipAddress, 0L);

        // Son bir saniye içindeki istek sayısını kontrol edin
        if (requestCount >= MAX_REQUESTS_PER_SECOND) {
            return true; // Potansiyel bot
        }

        // İstek sayısını güncelleyin
        requestCountMap.put(ipAddress, requestCount + 1);

        // Bir saniyeden daha eski istekleri temizleyin
        requestCountMap.entrySet().removeIf(entry -> currentTimeMillis - entry.getValue() > 1000);

        return false;
    }
}

