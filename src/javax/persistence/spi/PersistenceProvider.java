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
package javax.persistence.spi;

import java.util.Map;
import javax.persistence.EntityManagerFactory;
/**
 * Interface implemented by a persistence provider.
 * The implementation of this interface that is to
 * be used for a given {@link javax.persistence.EntityManager} is specified in
 * persistence.xml file in the persistence archive.
 * This interface is invoked by the Container when it
 * needs to create an {@link javax.persistence.EntityManagerFactory}, or by the
 * Persistence class when running outside the Container.
 *
 * @since Java Persistence 1.0
 */
public interface PersistenceProvider {

    /**
    * Called by Persistence class when an {@link javax.persistence.EntityManagerFactory}
    * is to be created.
    *
    * @param emName The name of the persistence unit
    * @param map A Map of properties for use by the
    * persistence provider. These properties may be used to
    * override the values of the corresponding elements in
    * the persistence.xml file or specify values for
    * properties not specified in the persistence.xml.
    * @return EntityManagerFactory for the persistence unit,
    * or null if the provider is not the right provider
    */
    public EntityManagerFactory createEntityManagerFactory(String emName, Map map);

    /**
     * Called by the container when an {@link javax.persistence.EntityManagerFactory}
     * is to be created.
     *
     * @param info Metadata for use by the persistence provider
     * @param map A Map of integration-level properties for use
     * by the persistence provider. Can be null if there is no
     * integration-level property.
     * @return EntityManagerFactory for the persistence unit
     * specified by the metadata
     */
    public EntityManagerFactory createContainerEntityManagerFactory(PersistenceUnitInfo info, Map map);
}
        
