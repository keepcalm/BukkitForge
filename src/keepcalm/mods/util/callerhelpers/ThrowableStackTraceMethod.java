package keepcalm.mods.util.callerhelpers;

/**
     * Get a stack trace from a new Throwable
     */
    public class ThrowableStackTraceMethod extends GetCallerClassNameMethod {

        public String getCallerClassName(int callStackDepth) {
            return new Throwable().getStackTrace()[callStackDepth].getClassName();
        }

        public String getMethodName() {
            return "Throwable StackTrace";
        }
    }
