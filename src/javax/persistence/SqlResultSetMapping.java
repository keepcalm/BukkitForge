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
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/** 
 * This annotation is used to specify the mapping of the result 
 * of a native SQL query.
 *
 * <pre>
 *    Example:
 *
 *    Query q = em.createNativeQuery(
 *        "SELECT o.id AS order_id, " +
 *            "o.quantity AS order_quantity, " +
 *            "o.item AS order_item, " +
 *            "i.name AS item_name, " +
 *        "FROM Order o, Item i " +
 *        "WHERE (order_quantity > 25) AND (order_item = i.id)",
 *    "OrderResults");
 *    
 *    &#064;SqlResultSetMapping(name="OrderResults", 
 *        entities={ 
 *            &#064;EntityResult(entityClass=com.acme.Order.class, fields={
 *                &#064;FieldResult(name="id", column="order_id"),
 *                &#064;FieldResult(name="quantity", column="order_quantity"), 
 *                &#064;FieldResult(name="item", column="order_item")})},
 *        columns={
 *            &#064;ColumnResult(name="item_name")}
 *    )
 * </pre>
 *
 * @since Java Persistence 1.0
 */
@Target({TYPE}) 
@Retention(RUNTIME)
public @interface SqlResultSetMapping { 

    /** 
     * The name given to the result set mapping, and used to refer 
     * to it in the methods of the {@link Query} API.
     */
    String name(); 

    /** Specifies the result set mapping to entities. */
    EntityResult[] entities() default {};

    /** Specifies the result set mapping to scalar values. */
    ColumnResult[] columns() default {};
}
