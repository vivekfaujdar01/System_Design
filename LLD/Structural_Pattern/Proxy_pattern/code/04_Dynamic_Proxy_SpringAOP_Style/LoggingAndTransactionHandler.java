import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;

public class LoggingAndTransactionHandler implements InvocationHandler {
    private final Object target;

    public LoggingAndTransactionHandler(Object target) {
        this.target = target;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        String methodName = method.getName();
        String argsString = (args != null) ? Arrays.toString(args) : "[]";

        System.out.println("\n---------------------------------------------------------");
        System.out.println("[SPRING-AOP DYNAMIC PROXY] Intercepted Method Invocation: " + methodName + argsString);
        long startTime = System.currentTimeMillis();

        boolean isWriteOperation = methodName.startsWith("transfer") || methodName.startsWith("update") || methodName.startsWith("save");

        if (isWriteOperation) {
            System.out.println("[AOP Aspect: @Transactional] BEGIN DATABASE TRANSACTION...");
        }

        Object result = null;
        try {
            // Delegate invocation to the real target object via reflection
            result = method.invoke(target, args);

            if (isWriteOperation) {
                System.out.println("[AOP Aspect: @Transactional] COMMIT TRANSACTION SUCCESSFUL.");
            }
        } catch (Throwable t) {
            Throwable cause = t.getCause() != null ? t.getCause() : t;
            if (isWriteOperation) {
                System.err.println("[AOP Aspect: @Transactional] ERROR DETECTED! ROLLING BACK TRANSACTION... Reason: " + cause.getMessage());
            }
            throw cause;
        } finally {
            long duration = System.currentTimeMillis() - startTime;
            System.out.println("[AOP Aspect: @Metrics] Method " + methodName + " executed in " + duration + "ms.");
            System.out.println("---------------------------------------------------------");
        }

        return result;
    }

    @SuppressWarnings("unchecked")
    public static <T> T createProxy(T target, Class<T> interfaceClass) {
        return (T) Proxy.newProxyInstance(
            interfaceClass.getClassLoader(),
            new Class<?>[]{ interfaceClass },
            new LoggingAndTransactionHandler(target)
        );
    }
}
