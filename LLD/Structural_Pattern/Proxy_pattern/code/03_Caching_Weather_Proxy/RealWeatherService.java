import java.util.Random;

public class RealWeatherService implements WeatherService {
    private final Random random = new Random();

    @Override
    public String getWeatherForecast(String city) {
        System.out.println("[RealWeatherService] Connecting to external satellite API for city: " + city + " (Simulating network latency)...");
        try {
            Thread.sleep(800); // Simulate network latency
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        int temp = 20 + random.nextInt(15);
        String report = "Sunny, " + temp + "°C (Fetched at " + System.currentTimeMillis() + ")";
        System.out.println("[RealWeatherService] Received report from satellite for " + city + ": " + report);
        return report;
    }
}
