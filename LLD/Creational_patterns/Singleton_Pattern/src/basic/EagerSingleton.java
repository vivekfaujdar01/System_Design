/**
 * VARIANT 1 — Eager Singleton
 *
 * The instance is created at class-load time.
 * Thread-safe by JVM guarantee (class initialization is atomic).
 * Use when: the instance is always needed and creation cost is low.
 */
public class EagerSingleton {

    // Created once when the class is first loaded into memory
    private static final EagerSingleton INSTANCE = new EagerSingleton();

    /** Private constructor — prevents any external instantiation */
    private EagerSingleton() {
        System.out.println("[EagerSingleton] Constructor called — instance created.");
    }

    /** The only public access point */
    public static EagerSingleton getInstance() {
        return INSTANCE;
    }

    public void showInfo() {
        System.out.println("[EagerSingleton] instance hashCode = " + System.identityHashCode(this));
    }
}
