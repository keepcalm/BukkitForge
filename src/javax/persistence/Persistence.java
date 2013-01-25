/*
 * The contents of this file are subject to the terms 
 * of the Common Development and Distribution License 
 * (the License).  You may not use this file except in
 * compliance with the License.
 * 
 * You can obtain a copy of the license at 
 * https://glassfish.dev.java.net/public/CDDLv1.0.html or
 * glassfish/bootstrap/legal/CDDLv1.0.txt.
 * See the License for the specific language governing 
 * permissions and limitations under the License.
 * 
 * When distributing Covered Code, include this CDDL 
 * Header Notice in each file and include the License file 
 * at glassfish/bootstrap/legal/CDDLv1.0.txt.  
 * If applicable, add the following below the CDDL Header, 
 * with the fields enclosed by brackets [] replaced by
 * you own identifying information: 
 * "Portions Copyrighted [year] [name of copyright owner]"
 * 
 * Copyright 2006 Sun Microsystems, Inc. All rights reserved.
 */
package javax.persistence;

// J2SE imports
import java.io.IOException;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Set;
import java.util.Map;
import java.util.HashSet;
import java.util.Enumeration;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

// persistence imports
import javax.persistence.spi.PersistenceProvider;

/**
 * Bootstrap class that is used to obtain an {@link EntityManagerFactory}.
 *
 * @since Java Persistence 1.0
 */
public class Persistence {

    public static String PERSISTENCE_PROVIDER = "javax.persistence.spi.PeristenceProvider";
    protected static final Set<PersistenceProvider> providers = new HashSet<PersistenceProvider>();
  
    /**
     * Create and return an EntityManagerFactory for the 
     * named persistence unit.
     * 
     * @param persistenceUnitName The name of the persistence unit
     * @return The factory that creates EntityManagers configured 
     * according to the specified persistence unit
     */
    public static EntityManagerFactory createEntityManagerFactory(String persistenceUnitName) {
        return createEntityManagerFactory(persistenceUnitName, null);
    }

    /**
     * Create and return an EntityManagerFactory for the 
     * named persistence unit using the given properties.
     * 
     * @param persistenceUnitName The name of the persistence unit
     * @param properties Additional properties to use when creating the 
     * factory. The values of these properties override any values
     * that may have been configured elsewhere.
     * @return The factory that creates EntityManagers configured 
     * according to the specified persistence unit.
     */
    public static EntityManagerFactory createEntityManagerFactory(
            String persistenceUnitName, Map properties) {
        EntityManagerFactory emf = null;
        if (providers.size() == 0) {
            try{
                findAllProviders();
            } catch (IOException exc){};
        }
        for (PersistenceProvider provider : providers) {
            emf = provider.createEntityManagerFactory(persistenceUnitName, properties);
            if (emf != null){
                break;
            }
        }
        if (emf == null) {
            throw new PersistenceException("No Persistence provider for EntityManager named " + persistenceUnitName);
        }
        return emf;
    }
  
    // Helper methods

    private static void findAllProviders() throws IOException {
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        Enumeration<URL> resources = 
            loader.getResources("META-INF/services/" + PersistenceProvider.class.getName());
        Set<String> names = new HashSet<String>();
        while (resources.hasMoreElements()) {
            URL url = resources.nextElement();
            InputStream is = url.openStream();
            try {
                names.addAll(providerNamesFromReader(new BufferedReader(new InputStreamReader(is))));
            } finally {
                is.close();
            }
        }
        for (String s : names) {
            try{
                providers.add((PersistenceProvider)loader.loadClass(s).newInstance());
            } catch (ClassNotFoundException exc){
            } catch (InstantiationException exc){
            } catch (IllegalAccessException exc){
            }
        }
    }

    private static final Pattern nonCommentPattern = Pattern.compile("^([^#]+)");

    private static Set<String> providerNamesFromReader(BufferedReader reader) throws IOException {
        Set<String> names = new HashSet<String>();
        String line;
        while ((line = reader.readLine()) != null) {
            line = line.trim();
            Matcher m = nonCommentPattern.matcher(line);
            if (m.find()) {
                names.add(m.group().trim());
            }
        }
        return names;
    }
}
