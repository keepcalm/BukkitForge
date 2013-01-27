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

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/** 
 * Is used to specify a named query in the Java Persistence query language, 
 * which is a static query expressed in metadata.
 * Query names are scoped to the persistence unit.
 *
 * <p> The following is an example of the definition of a named query 
 * in the Java Persistence query language:
 *
 * <pre>
 *    &#064;NamedQuery(
 *            name="findAllCustomersWithName",
 *            query="SELECT c FROM Customer c WHERE c.name LIKE :custName"
 *    )
 * </pre>
 *
 * <p> The following is an example of the use of a named query:
 *
 * <pre>
 *    &#064;PersistenceContext
 *    public EntityManager em;
 *    ...
 *    customers = em.createNamedQuery("findAllCustomersWithName")
 *            .setParameter("custName", "Smith")
 *            .getResultList();
 * </pre>
 *
 * @since Java Persistence 1.0
 */
@Target({TYPE}) 
@Retention(RUNTIME)
public @interface NamedQuery {

    /** 
     * Refers to the query when using the {@link EntityManager} 
     * methods that create query objects. 
     */
    String name() default "";

    /** The query string in the Java Persistence query language */
    String query();

    /** Vendor-specific query hints */
    QueryHint[] hints() default {};
}
