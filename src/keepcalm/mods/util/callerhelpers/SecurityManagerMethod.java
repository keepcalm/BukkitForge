package keepcalm.mods.util.callerhelpers;

/**
     * Use the SecurityManager.getClassContext()
     */
    public class SecurityManagerMethod extends GetCallerClassNameMethod {
        public String  getCallerClassName(int callStackDepth) {
            return mySecurityManager.getCallerClassName(callStackDepth);
        }

        public String getMethodName() {
            return "SecurityManager";
        }

        /**
         * A custom security manager that exposes the getClassContext() information
         */
         class MySecurityManager extends SecurityManager {
            public String getCallerClassName(int callStackDepth) {
                return getClassContext()[callStackDepth].getName();
            }
        }

        private final MySecurityManager mySecurityManager =
                new MySecurityManager();
    }
