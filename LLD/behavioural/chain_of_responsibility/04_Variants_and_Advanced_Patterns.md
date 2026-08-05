# 🔀 Variants & Advanced Concepts

The Chain of Responsibility pattern comes in several architectural flavors depending on system requirements.

---

## 1. Pure Chain vs. Filter/Interceptor Chain

There are two primary paradigms when designing a Chain of Responsibility:

```
PARADIGM 1: Pure Chain (Single Handler Processing)
Client ──> Handler A (Can't handle) ──> Handler B (HANDLES & STOPS!) ──x (Handler C skipped)

PARADIGM 2: Interceptor / Filter Chain (All Handlers Process)
Client ──> Handler A (Processes & passes) ──> Handler B (Processes & passes) ──> Handler C (Processes)
```

### Detailed Comparison

| Feature | Pure Chain of Responsibility | Interceptor / Filter Chain |
|---------|-----------------------------|---------------------------|
| **Execution Goal** | Exactly **one** handler processes the request. | **Multiple or all** handlers process the request in sequence. |
| **Propagation** | Handler stops passing once it successfully processes. | Handler processes and explicitly forwards to `next.handle()`. |
| **Classic Example** | Tech Support Escalation, ATM Dispenser. | Servlet Filters, Express.js Middleware, Spring Security. |
| **Short-Circuiting** | Natural: handled = stop. | Conditional: stops only if validation/auth fails. |

---

## 2. Dynamic Chain Modification at Runtime

In complex enterprise architectures, the chain shouldn't be hardcoded. You often need to add, remove, or reorder handlers dynamically (e.g., plugin architectures).

### Dynamic Chain Manager Implementation

```java
import java.util.ArrayList;
import java.util.List;

public class DynamicChainManager<T> {
    private final List<ChainHandler<T>> handlers = new ArrayList<>();

    public DynamicChainManager<T> addHandler(ChainHandler<T> handler) {
        this.handlers.add(handler);
        return this;
    }

    public boolean removeHandler(ChainHandler<T> handler) {
        return this.handlers.remove(handler);
    }

    public void executeChain(T context) {
        for (ChainHandler<T> handler : handlers) {
            boolean shouldContinue = handler.process(context);
            if (!shouldContinue) {
                System.out.println("Chain execution stopped early by handler: " + handler.getClass().getSimpleName());
                break;
            }
        }
    }
}

@FunctionalInterface
interface ChainHandler<T> {
    // Returns true to continue to next handler, false to short-circuit
    boolean process(T context);
}
```

---

## 3. Composite Chain (Tree of Responsibility)

What if requests don't move in a linear line, but down a hierarchical tree?

For instance, in a Graphical User Interface (GUI) system (like web DOM or Android UI):
* A click event happens on a **Button**.
* If Button doesn't handle click, event bubbles up to parent **Panel**.
* If Panel doesn't handle click, bubbles up to **Dialog Window**.
* If Dialog doesn't handle, bubbles up to root **Application Window**.

```
             [ Root Window ]
                   ▲
                   │ (parent)
             [ Dialog Box ]
                   ▲
                   │ (parent)
             [ Panel Component ]
                   ▲
                   │ (parent)
             [ Submit Button ] <--- Click Event Triggered Here!
```

This combines **Composite Pattern** (UI Component Tree) with **Chain of Responsibility** (Bubbling Event Delegation).

---

## 4. Chain of Responsibility + Command Pattern

Handlers in the chain often encapsulate complex business operations. Instead of writing logic directly inside handlers, each handler can execute a **Command Object**.

```java
public class CommandExecutingHandler extends BaseHandler {
    private final Command commandToExecute;

    public CommandExecutingHandler(Command command) {
        this.commandToExecute = command;
    }

    @Override
    public void handle(Request req) {
        if (shouldExecute(req)) {
            commandToExecute.execute();
        }
        if (next != null) {
            next.handle(req);
        }
    }
}
```

---

## 5. Asynchronous Chain of Responsibility (`CompletableFuture` / Promises)

In high-throughput non-blocking reactive frameworks (Netty, WebFlux, Node.js), handlers perform asynchronous I/O (DB calls, external API calls). Chaining must support async futures.

```java
import java.util.concurrent.CompletableFuture;

public interface AsyncHandler<T> {
    CompletableFuture<T> handleAsync(T context);
}

public class AsyncAuthHandler implements AsyncHandler<RequestContext> {
    private AsyncHandler<RequestContext> next;

    @Override
    public CompletableFuture<RequestContext> handleAsync(RequestContext ctx) {
        return authenticateAsync(ctx.getToken())
            .thenCompose(isAuthenticated -> {
                if (!isAuthenticated) {
                    ctx.setFailed(true);
                    return CompletableFuture.completedFuture(ctx);
                }
                if (next != null) {
                    return next.handleAsync(ctx);
                }
                return CompletableFuture.completedFuture(ctx);
            });
    }

    private CompletableFuture<Boolean> authenticateAsync(String token) {
        // Simulate async DB/Redis lookup
        return CompletableFuture.supplyAsync(() -> token != null && token.startsWith("VALID"));
    }
}
```
