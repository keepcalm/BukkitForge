package keepcalm.mods.util.callerhelpers;

/**
 * Abstract class for testing different methods of getting the caller class name
 */
public abstract class GetCallerClassNameMethod {
    public abstract String getCallerClassName(int callStackDepth);
    public abstract String getMethodName();
}
