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

import java.lang.annotation.Target;
import java.lang.annotation.Retention;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * References an entity in the SELECT clause of a SQL query.
 * If this annotation is used, the SQL statement should select 
 * all of the columns that are mapped to the entity object. 
 * This should include foreign key columns to related entities. 
 * The results obtained when insufficient data is available 
 * are undefined.
 *
 * <pre>
 *   Example
 *   Query q = em.createNativeQuery(
 *       "SELECT o.id, o.quantity, o.item, i.id, i.name, i.description "+
 *           "FROM Order o, Item i " +
 *           "WHERE (o.quantity > 25) AND (o.item = i.id)",
 *       "OrderItemResults");
 *   &#064;SqlResultSetMapping(name="OrderItemResults",
 *       entities={
 *           &#064;EntityResult(entityClass=com.acme.Order.class),
 *           &#064;EntityResult(entityClass=com.acme.Item.class)
 *   })
 * </pre>
 *
 * @since Java Persistence 1.0
 */
@Target({}) 
@Retention(RUNTIME)

public @interface EntityResult { 

    /** The class of the result */
    Class entityClass(); 

    /** 
     * Maps the columns specified in the SELECT list of the 
     * query to the properties or fields of the entity class. 
     */
    FieldResult[] fields() default {};

    /** 
     * Specifies the column name (or alias) of the column in 
     * the SELECT list that is used to determine the type of 
     * the entity instance.
     */
    String discriminatorColumn() default "";
}
