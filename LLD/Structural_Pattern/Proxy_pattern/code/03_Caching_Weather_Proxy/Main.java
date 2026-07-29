public class Main {
    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== CACHING PROXY DEMO ===");

        WeatherService realService = new RealWeatherService();
        // Create proxy with 2-second TTL
        WeatherService proxyService = new CachingWeatherProxy(realService, 2);

        System.out.println("\n1. First call for 'Tokyo' (Cache Miss -> Calls Remote API):");
        System.out.println("Result: " + proxyService.getWeatherForecast("Tokyo"));

        System.out.println("\n2. Immediate second call for 'Tokyo' (Cache Hit -> Sub-millisecond response):");
        System.out.println("Result: " + proxyService.getWeatherForecast("Tokyo"));

        System.out.println("\n3. First call for 'London' (Cache Miss -> Calls Remote API):");
        System.out.println("Result: " + proxyService.getWeatherForecast("London"));

        System.out.println("\n4. Waiting 2.5 seconds for TTL expiration...");
        Thread.sleep(2500);

        System.out.println("\n5. Call for 'Tokyo' after TTL expired (Cache Expired -> Refetches from API):");
        System.out.println("Result: " + proxyService.getWeatherForecast("Tokyo"));
    }
}
