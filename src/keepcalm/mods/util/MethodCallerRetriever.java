package keepcalm.mods.util;

import keepcalm.mods.util.callerhelpers.*;

/**
 * Created with IntelliJ IDEA.
 * User: jtdossett
 * Date: 3/14/13
 * Time: 12:10 AM
 * To change this template use File | Settings | File Templates.
 */
public class MethodCallerRetriever {

        private static GetCallerClassNameMethod instance = null;

        public static GetCallerClassNameMethod instance() {

            if( instance == null ) {
            try {
                Class.forName("sun.reflect.Reflection");
            }
            catch (ClassNotFoundException exception) {
                instance = new SecurityManagerMethod();
            }
            }

            return instance;
        }
}

