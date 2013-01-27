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
 * This annotation is used to override a many-to-one or
 * one-to-one mapping of property or field for an entity relationship.
 *
 * <p> The <code>AssociationOverride</code> annotation may be applied
 * to an entity that extends a mapped superclass to override a many-to-one
 * or one-to-one mapping defined by the mapped superclass. If the
 * <code>AssociationOverride</code> annotation is not specified, the join
 * column is mapped the same as in the original mapping.
 * <pre>
 *    Example:
 *    &#064;MappedSuperclass
 *    public class Employee {
 *        ...
 *        &#064;ManyToOne
 *        protected Address address;
 *        ...
 *    }
 *    
 *    &#064;Entity 
 *    &#064;AssociationOverride(name="address", 
 *        joinColumns=&#064;JoinColumn(name="ADDR_ID"))
 *    // address field mapping overridden to ADDR_ID fk
 *    public class PartTimeEmployee extends Employee {
 *        ...
 *    }
 * </pre>
 *
 * @see OneToOne
 * @see ManyToOne
 * @see MappedSuperclass
 *
 * @since Java Persistence 1.0
 */
@Target({TYPE, METHOD, FIELD}) 
@Retention(RUNTIME)

public @interface AssociationOverride {

    /**
     * The name of the relationship property whose mapping is
     * being overridden if property-based access is being used,
     * or the name of the relationship field if field-based access is used.
     */
    String name();

    /**
     * The join column that is being mapped to the persistent
     * attribute. The mapping type will remain the same as is defined
     * in the mapped superclass.
     */
    JoinColumn[] joinColumns();
}
