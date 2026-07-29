import java.util.HashMap;
import java.util.Map;

public class CachingWeatherProxy implements WeatherService {
    private static class CacheEntry {
        final String data;
        final long timestamp;

        CacheEntry(String data, long timestamp) {
            this.data = data;
            this.timestamp = timestamp;
        }
    }

    private final WeatherService realService;
    private final Map<String, CacheEntry> cache = new HashMap<>();
    private final long timeToLiveMillis;

    public CachingWeatherProxy(WeatherService realService, long timeToLiveSeconds) {
        this.realService = realService;
        this.timeToLiveMillis = timeToLiveSeconds * 1000;
    }

    @Override
    public String getWeatherForecast(String city) {
        String cacheKey = city.toLowerCase();
        long now = System.currentTimeMillis();

        if (cache.containsKey(cacheKey)) {
            CacheEntry entry = cache.get(cacheKey);
            if (now - entry.timestamp < timeToLiveMillis) {
                System.out.println("[CachingProxy] CACHE HIT for '" + city + "'! Returning cached weather report instantly.");
                return entry.data;
            } else {
                System.out.println("[CachingProxy] CACHE EXPIRED for '" + city + "' (TTL passed). Evicting stale entry...");
                cache.remove(cacheKey);
            }
        }

        System.out.println("[CachingProxy] CACHE MISS for '" + city + "'. Delegating call to RealWeatherService...");
        String freshData = realService.getWeatherForecast(city);
        cache.put(cacheKey, new CacheEntry(freshData, now));
        return freshData;
    }
}
