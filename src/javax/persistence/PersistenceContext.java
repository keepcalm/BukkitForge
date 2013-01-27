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

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Expresses a dependency on an {@link EntityManager} persistence context.
 *
 * @since Java Persistence 1.0
 */

@Target({TYPE, METHOD, FIELD})
@Retention(RUNTIME)
public @interface PersistenceContext {

    /**
     * The name by which the entity manager is to be accessed in the 
     * environment referencing context, and is not needed when dependency 
     * injection is used.
     */
    String name() default "";

    /**
     * The name of the persistence unit. If the unitName element is 
     * specified, the persistence unit for the entity manager that 
     * is accessible in JNDI must have the same name.
     */
    String unitName() default "";

    /**
     * Specifies whether this is a transaction-scoped persistence context 
     * or an extended persistence context.
     */
    PersistenceContextType type() default PersistenceContextType.TRANSACTION;

    /**
     * Used to specify properties for the container or persistence
     * provider.  Vendor specific properties may be included in this
     * set of properties.  Properties that are not recognized by
     * a vendor are ignored.  
     */ 
    PersistenceProperty[] properties() default {};
}
