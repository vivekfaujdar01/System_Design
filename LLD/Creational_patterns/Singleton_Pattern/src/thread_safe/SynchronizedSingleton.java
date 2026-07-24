/**
 * VARIANT 3 — Synchronized Method Singleton
 *
 * Adds `synchronized` keyword to getInstance() so only one thread
 * can execute it at a time — guarantees thread safety.
 *
 * ⚠️ Performance trade-off:
 *    The lock is acquired on EVERY call, even after the instance
 *    already exists. Under high concurrency this becomes a bottleneck.
 *
 * Use when: simplicity is preferred over maximum throughput.
 */
public class SynchronizedSingleton {

    private static SynchronizedSingleton instance;

    private SynchronizedSingleton() {
        System.out.println("[SynchronizedSingleton] Constructor called — instance created.");
    }

    /**
     * ✅ Thread-safe — only one thread enters at a time.
     * ⚠️ Slow — lock acquired on every call after instance exists.
     */
    public static synchronized SynchronizedSingleton getInstance() {
        if (instance == null) {
            instance = new SynchronizedSingleton();
        }
        return instance;
    }

    public void showInfo() {
        System.out.println("[SynchronizedSingleton] instance hashCode = " + System.identityHashCode(this));
    }
}
