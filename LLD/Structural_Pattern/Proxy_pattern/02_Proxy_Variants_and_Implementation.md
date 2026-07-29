# Module 2: Proxy Variants & Implementation Deep-Dive

## 1. Overview of Proxy Variants

While all proxies implement the common `Subject` interface, their internal mechanisms differ significantly based on their variant type. This module examines the technical implementation strategies, internal workflows, and architectural code patterns for each proxy variant.

---

## 2. Virtual Proxy (Lazy Loading & On-Demand Initialization)

### Problem Statement
Instantiating heavy resources upfront (e.g., rendering thousands of high-resolution images in a gallery, initializing database connections during startup, or loading heavy AI model weights) consumes excessive RAM and creates startup lag, even if the user never accesses those specific resources.

### How Virtual Proxy Works
1. The Proxy maintains a `null` reference to the `RealSubject`.
2. When the client calls a method on the `Proxy`, the Proxy checks if the `RealSubject` instance exists.
3. If `null`, it instantiates the `RealSubject` **on-demand** (lazy initialization).
4. Subsequent calls reuse the already-created `RealSubject`.

```text
CLIENT CALL: displayImage()
    │
    ▼
┌────────────────────────────────────────────────────────┐
│ PROXY: HighResImageProxy                               │
│                                                        │
│ Is realImage == null?                                  │
│   ├── YES ──► Instantiate RealImage("photo.png")        │
│   │           (Loads 50MB file into memory)            │
│   └── NO  ──► Skip creation (Reuse instance)           │
│                                                        │
│ Delegate: realImage.displayImage()                     │
└────────────────────────────────────────────────────────┘
```

### Thread Safety Consideration
When multiple threads access a Virtual Proxy simultaneously, lazy initialization can lead to race conditions where multiple instances of `RealSubject` are created. Use **Double-Checked Locking** or synchronized blocks in multi-threaded environments:

```java
public class ThreadSafeVirtualProxy implements Subject {
    private volatile RealSubject realSubject;

    @Override
    public void request() {
        if (realSubject == null) {
            synchronized (this) {
                if (realSubject == null) {
                    realSubject = new RealSubject(); // Heavy operation
                }
            }
        }
        realSubject.request();
    }
}
```

---

## 3. Protection Proxy (Access Control & Authorization)

### Problem Statement
Certain operations (e.g., deleting a database record, modifying financial accounts, or executing system commands) must be restricted based on user credentials, authentication tokens, or role-based access control (RBAC).

### How Protection Proxy Works
1. The Proxy accepts the `User` context or authentication token during construction or method invocation.
2. Before delegating the call to `RealSubject`, it validates permissions.
3. If authorized, it executes the operation. Otherwise, it throws an `AccessDeniedException` or returns an unauthorized status code.

```text
                       ┌──────────────────────────────────┐
                       │    ProtectionProxyDatabase       │
                       ├──────────────────────────────────┤
Client: executeQuery() │ Check user.getRole():            │
──────────────────────►│  - Role == ADMIN ──► Delegate    │
                       │  - Role == USER  ──► Check query │
                       │  - Role == GUEST ──► Throw Error │
                       └──────────────────────────────────┘
```

---

## 4. Caching Proxy (Performance Optimization & TTL Management)

### Problem Statement
Repeated requests for computationally expensive or network-intensive operations (e.g., querying external weather APIs, calculating complex analytics, or executing heavy SQL queries) waste bandwidth and CPU.

### How Caching Proxy Works
1. The Proxy contains an internal cache map (e.g., `ConcurrentHashMap<Key, CacheEntry>`).
2. When a request comes in, the Proxy constructs a unique cache key based on input parameters.
3. It checks if a valid (non-expired) entry exists in the cache:
   * **Cache Hit**: Returns cached result immediately.
   * **Cache Miss / Expired**: Delegates to `RealSubject`, stores result in cache with timestamp, and returns result.

```text
Client Request ──► [ Caching Proxy ]
                          │
            ┌─────────────┴─────────────┐
            ▼                           ▼
    [ Key in Cache? ]           [ Not in Cache / Expired ]
            │                           │
    YES ────┤ (Cache Hit)       NO ─────┤ (Cache Miss)
            │                           │
     Return Cached Data          Fetch from RealSubject
                                 Store in Cache
                                 Return Data
```

---

## 5. Remote Proxy (Distributed Systems & RPC Stubs)

### Problem Statement
In distributed architectures, microservices, or client-server applications, an object resides on a remote server across a network. Writing socket code, TCP handlers, and HTTP requests inside business domain logic creates severe coupling.

### How Remote Proxy Works
1. The client invokes methods locally on a **Stub Proxy** as if it were a local object.
2. The Remote Proxy serializes parameters (marshalling), transmits them over TCP/HTTP to the remote server skeleton/host.
3. The remote host executes the call on the `RealSubject`, serializes the return value, and sends it back across the network.
4. The Proxy deserializes (unmarshalls) the response and returns it to the client transparently.

```text
CLIENT JVM                                       REMOTE SERVER JVM
┌──────────┐     ┌───────────────┐               ┌───────────────┐     ┌─────────────┐
│  Client  │ ──► │  RemoteStub   │ ── Network ─► │ RemoteSkeleton│ ──► │ RealSubject │
│  Code    │     │  (Proxy)      │   (gRPC/RMI)  │ (Dispatcher)  │     │  Object     │
└──────────┘     └───────────────┘               └───────────────┘     └─────────────┘
```

---

## 6. Smart Reference & Logging Proxy

### Problem Statement
Systems need non-functional infrastructure concerns like tracking active object references, locking resources during execution, recording audit trails, or benchmarking method duration without cluttering domain logic.

### Responsibilities
* **Reference Counting**: Tracks how many clients hold references to a heavy shared object; frees resources when count reaches 0.
* **Audit & Performance Logging**: Records execution start/end timestamps, input args, user IDs, and elapsed milliseconds for monitoring (APM).

---

## 7. Static Proxy vs. Dynamic Proxy

In object-oriented languages like Java, proxies can be categorized by **when** the proxy class is generated:

```text
┌─────────────────────────────────────────────────────────────────────────────┐
│                     STATIC PROXY vs DYNAMIC PROXY                           │
├───────────────────────────┬─────────────────────────────────────────────────┤
│ Aspect                    │ Static Proxy                                    │ Dynamic Proxy                           │
├───────────────────────────┼─────────────────────────────────────────────────┼─────────────────────────────────────────┤
│ Class Creation Time       │ Compile-time (written by developer)             │ Runtime (generated programmatically)    │
│ Class Count               │ 1 Proxy class per Subject interface             │ 1 Handler can proxy ANY interface       │
│ Maintenance Effort        │ High (adding method to interface breaks proxy)  │ Low (handler intercepts all methods)    │
│ Performance               │ Slightly faster (direct Java method calls)      │ Minimal reflection overhead             │
│ Primary Usage             │ Simple hand-crafted LLD patterns                │ Frameworks (Spring AOP, Hibernate, Mockito)│
└───────────────────────────┴─────────────────────────────────────────────────┴─────────────────────────────────────────┘
```

### Static Proxy Example Blueprint (Hand-coded)
```java
public class StaticProxy implements Service {
    private final Service realService;
    public StaticProxy(Service realService) { this.realService = realService; }

    @Override
    public void execute() {
        // Pre-processing
        realService.execute();
        // Post-processing
    }
}
```

### Dynamic Proxy Blueprint in Java (`java.lang.reflect.Proxy`)
Java provides built-in dynamic proxy generation via `java.lang.reflect.Proxy` and `InvocationHandler`:

```java
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class DynamicLoggingHandler implements InvocationHandler {
    private final Object target;

    public DynamicLoggingHandler(Object target) {
        this.target = target;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        System.out.println("[LOG] Executing method: " + method.getName());
        long start = System.currentTimeMillis();
        
        Object result = method.invoke(target, args); // Delegates to real object
        
        long duration = System.currentTimeMillis() - start;
        System.out.println("[LOG] Finished " + method.getName() + " in " + duration + "ms");
        return result;
    }

    @SuppressWarnings("unchecked")
    public static <T> T createProxy(T target, Class<T> interfaceClass) {
        return (T) Proxy.newProxyInstance(
            interfaceClass.getClassLoader(),
            new Class<?>[]{ interfaceClass },
            new DynamicLoggingHandler(target)
        );
    }
}
```

---

## 8. Summary Checklist for Module 2

* [x] **Virtual Proxy**: Lazy initialization with optional thread synchronization.
* [x] **Protection Proxy**: Authorization & role-based validation before execution.
* [x] **Caching Proxy**: TTL-based cache lookup to reduce compute/network calls.
* [x] **Remote Proxy**: Marshalling and network transport for RPC/microservices.
* [x] **Static vs Dynamic**: Static proxies are compile-time handcrafted; Dynamic proxies are runtime framework components (Spring AOP / Reflection).
