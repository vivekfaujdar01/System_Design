/**
 * VARIANT 4 — Double-Checked Locking (DCL) Singleton
 *
 * Optimises the Synchronized variant by only locking during creation.
 * After the instance exists, all subsequent calls return it WITHOUT
 * acquiring any lock.
 *
 * ⚠️ `volatile` is MANDATORY (Java 5+).
 *    Without it, the JVM may publish a partially-constructed object
 *    to other threads due to instruction reordering.
 *
 * How it works:
 *   1st check (no lock)  → avoids lock acquisition if already initialised
 *   synchronized block   → serialises competing threads during first creation
 *   2nd check (in lock)  → prevents duplicate creation if two threads
 *                          both passed check-1 before either acquired the lock
 *
 * Use when: maximum throughput + lazy initialisation is required.
 */
public class DCLSingleton {

    /**
     * `volatile` ensures that writes to `instance` are visible to all threads
     * and that the write is NOT reordered before the constructor completes.
     */
    private static volatile DCLSingleton instance;

    private DCLSingleton() {
        System.out.println("[DCLSingleton] Constructor called — instance created.");
    }

    public static DCLSingleton getInstance() {
        if (instance == null) {                     // 1st check — fast path, no lock
            synchronized (DCLSingleton.class) {     // lock only for creation
                if (instance == null) {             // 2nd check — inside the lock
                    instance = new DCLSingleton();
                }
            }
        }
        return instance;
    }

    public void showInfo() {
        System.out.println("[DCLSingleton] instance hashCode = " + System.identityHashCode(this));
    }
}
