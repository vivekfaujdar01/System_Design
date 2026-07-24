/**
 * VARIANT 5 — Bill Pugh Singleton (Initialization-on-Demand Holder)
 *
 * Considered the CLEANEST lazy + thread-safe Singleton in Java.
 * No `synchronized`, no `volatile` — relies purely on JVM class-loading guarantees.
 *
 * How it works:
 *   - The JVM loads the outer class (BillPughSingleton) without touching
 *     the inner class (SingletonHolder).
 *   - SingletonHolder is only loaded when getInstance() is first called.
 *   - JVM class initialisation is atomic — only one thread initialises a class.
 *   - The static final INSTANCE field is written exactly once, safely.
 *
 * Named after Bill Pugh who proposed this idiom in 2004.
 *
 * Use when: you want the cleanest, most readable lazy + thread-safe Singleton.
 */
public class BillPughSingleton {

    private BillPughSingleton() {
        System.out.println("[BillPughSingleton] Constructor called — instance created.");
    }

    /**
     * Inner static class — NOT loaded until getInstance() is called for the first time.
     * JVM guarantees that this class is initialised atomically.
     */
    private static class SingletonHolder {
        private static final BillPughSingleton INSTANCE = new BillPughSingleton();
    }

    /**
     * First call to this method triggers the loading of SingletonHolder,
     * which triggers the creation of INSTANCE.
     * All subsequent calls return the already-initialised INSTANCE immediately.
     */
    public static BillPughSingleton getInstance() {
        return SingletonHolder.INSTANCE;
    }

    public void showInfo() {
        System.out.println("[BillPughSingleton] instance hashCode = " + System.identityHashCode(this));
    }
}
