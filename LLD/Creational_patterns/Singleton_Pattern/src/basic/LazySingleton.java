/**
 * VARIANT 2 — Lazy Singleton (Naïve — NOT Thread-Safe)
 *
 * Instance is created only on the first call to getInstance().
 * ⚠️ NOT safe for multi-threaded environments.
 *
 * Problem:
 *   Thread-1 checks instance == null → true
 *   Thread-2 checks instance == null → true (before Thread-1 assigns)
 *   Both threads create a separate instance → Singleton is broken.
 *
 * Use only in: strictly single-threaded programs.
 */
public class LazySingleton {

    private static LazySingleton instance;   // null until first call

    /** Private constructor */
    private LazySingleton() {
        System.out.println("[LazySingleton] Constructor called — instance created.");
    }

    /**
     * ❌ NOT thread-safe — two threads can both pass the null-check
     *    and each create their own instance.
     */
    public static LazySingleton getInstance() {
        if (instance == null) {
            instance = new LazySingleton();
        }
        return instance;
    }

    public void showInfo() {
        System.out.println("[LazySingleton] instance hashCode = " + System.identityHashCode(this));
    }
}
