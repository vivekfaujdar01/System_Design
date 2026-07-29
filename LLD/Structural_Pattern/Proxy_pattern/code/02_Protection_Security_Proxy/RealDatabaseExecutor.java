public class RealDatabaseExecutor implements DatabaseExecutor {
    @Override
    public String executeQuery(User user, String query) {
        System.out.println("[RealDBExecutor] Executing SQL statement directly on DB engine for " + user.getUsername() + ": '" + query + "'");
        return "SUCCESS: Query executed -> [" + query + "]";
    }
}
