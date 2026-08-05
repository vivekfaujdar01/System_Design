// Demo Application
public class LoggerChainDemo {
    private static Logger getChainOfLoggers() {
        Logger errorLogger = new ErrorLogger(Logger.ERROR);
        Logger debugLogger = new DebugLogger(Logger.DEBUG);
        Logger infoLogger = new InfoLogger(Logger.INFO);

        infoLogger.setNextLogger(debugLogger);
        debugLogger.setNextLogger(errorLogger);

        return infoLogger;
    }

    public static void main(String[] args) {
        Logger loggerChain = getChainOfLoggers();

        System.out.println("--- Sending INFO Message ---");
        loggerChain.logMessage(Logger.INFO, "This is an information message.");

        System.out.println("\n--- Sending DEBUG Message ---");
        loggerChain.logMessage(Logger.DEBUG, "This is a debug level information.");

        System.out.println("\n--- Sending ERROR Message ---");
        loggerChain.logMessage(Logger.ERROR, "This is a critical error message!");
    }
}

// Abstract Handler
abstract class Logger {
    public static int INFO = 1;
    public static int DEBUG = 2;
    public static int ERROR = 3;

    protected int level;
    protected Logger nextLogger;

    public void setNextLogger(Logger nextLogger) {
        this.nextLogger = nextLogger;
    }

    public void logMessage(int level, String message) {
        if (this.level <= level) {
            write(message);
        }
        if (nextLogger != null) {
            nextLogger.logMessage(level, message);
        }
    }

    abstract protected void write(String message);
}

// Concrete Handler 1
class InfoLogger extends Logger {
    public InfoLogger(int level) {
        this.level = level;
    }

    @Override
    protected void write(String message) {
        System.out.println("[INFO LOGGER]: " + message);
    }
}

// Concrete Handler 2
class DebugLogger extends Logger {
    public DebugLogger(int level) {
        this.level = level;
    }

    @Override
    protected void write(String message) {
        System.out.println("[DEBUG LOGGER]: " + message);
    }
}

// Concrete Handler 3
class ErrorLogger extends Logger {
    public ErrorLogger(int level) {
        this.level = level;
    }

    @Override
    protected void write(String message) {
        System.out.println("[ERROR LOGGER]: " + message);
    }
}
