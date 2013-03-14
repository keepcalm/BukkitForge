package keepcalm.mods.util.callerhelpers;

/**
     * Uses the internal Reflection class
     */
    public class ReflectionMethod extends GetCallerClassNameMethod {
        public String getCallerClassName(int callStackDepth) {
            return sun.reflect.Reflection.getCallerClass(callStackDepth).getName();
        }

        public String getMethodName() {
            return "Reflection";
        }
    }
