import java.util.HashMap;
import java.util.Map;

public class ProtectionProxyDatabaseExecutor implements DatabaseExecutor {
    private final DatabaseExecutor realExecutor;
    private final Map<String, Integer> userQueryCount = new HashMap<>();
    private static final int MAX_QUERIES_PER_USER = 3;

    public ProtectionProxyDatabaseExecutor(DatabaseExecutor realExecutor) {
        this.realExecutor = realExecutor;
    }

    @Override
    public String executeQuery(User user, String query) throws Exception {
        System.out.println("\n[ProtectionProxy] Intercepted query request from user: " + user.getUsername() + " (" + user.getRole() + ")");

        // 1. Destructive query guard
        String upperQuery = query.toUpperCase();
        if (upperQuery.contains("DROP") || upperQuery.contains("TRUNCATE")) {
            System.err.println("[ProtectionProxy] BLOCKED: Destructive statement detected!");
            throw new SecurityException("Access Denied: Destructive operations (DROP/TRUNCATE) are forbidden!");
        }

        // 2. Role-based authorization
        if (user.getRole() == User.Role.GUEST) {
            System.err.println("[ProtectionProxy] BLOCKED: GUEST role has no DB privileges!");
            throw new SecurityException("Access Denied: GUEST users are not permitted to access database.");
        }

        if (user.getRole() == User.Role.USER && !upperQuery.startsWith("SELECT")) {
            System.err.println("[ProtectionProxy] BLOCKED: Standard USER role can only execute SELECT queries!");
            throw new SecurityException("Access Denied: Standard USERs cannot execute write operations (INSERT/UPDATE/DELETE).");
        }

        // 3. Rate limiting check
        int currentCount = userQueryCount.getOrDefault(user.getUsername(), 0);
        if (currentCount >= MAX_QUERIES_PER_USER) {
            System.err.println("[ProtectionProxy] BLOCKED: Rate limit exceeded for user: " + user.getUsername());
            throw new IllegalStateException("Rate Limit Exceeded: Maximum " + MAX_QUERIES_PER_USER + " queries allowed per session.");
        }

        // Increment count and delegate call
        userQueryCount.put(user.getUsername(), currentCount + 1);
        System.out.println("[ProtectionProxy] Authorization PASSED. Delegating call to RealDatabaseExecutor...");
        return realExecutor.executeQuery(user, query);
    }
}
