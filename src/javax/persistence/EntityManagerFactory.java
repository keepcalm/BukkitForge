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

import java.util.Map;

/**
 * The <code>EntityManagerFactory</code> interface is used 
 * by the application to obtain an application-managed entity 
 * manager. When the application has finished using the entity 
 * manager factory, and/or at application shutdown, the 
 * application should close the entity manager factory. 
 * Once an <code>EntityManagerFactory</code> has been closed, all its entity 
 * managers are considered to be in the closed state.
 *
 * @since Java Persistence 1.0
 */
public interface EntityManagerFactory {

    /**
     * Create a new EntityManager.
     * This method returns a new EntityManager instance each time
     * it is invoked.
     * The isOpen method will return true on the returned instance.
     */
    EntityManager createEntityManager();

    /**
     * Create a new EntityManager with the specified Map of
     * properties.
     * This method returns a new EntityManager instance each time
     * it is invoked.
     * The isOpen method will return true on the returned instance.
     */
    EntityManager createEntityManager(Map map);

    /**
     * Close the factory, releasing any resources that it holds.
     * After a factory instance is closed, all methods invoked on
     * it will throw an IllegalStateException, except for isOpen,
     * which will return false. Once an EntityManagerFactory has
     * been closed, all its entity managers are considered to be
     * in the closed state.
     */
    void close();

    /**
    * Indicates whether or not this factory is open. Returns true
    * until a call to close has been made.
    */
    public boolean isOpen();
}
