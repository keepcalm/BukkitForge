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
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * The <code>AttributeOverride</code> annotation is used to 
 * override the mapping of a {@link Basic} (whether explicit or 
 * default) property or field or Id property or field.
 *
 * <p> The <code>AttributeOverride</code> annotation may be 
 * applied to an entity that extends a mapped superclass or to 
 * an embedded field or property to override a basic mapping 
 * defined by the mapped superclass or embeddable class. If the 
 * <code>AttributeOverride</code> annotation is not specified, 
 * the column is mapped the same as in the original mapping.
 *
 * <pre>
 * <p> Example:
 *
 *   &#064;MappedSuperclass
 *   public class Employee {
 *       &#064;Id protected Integer id;
 *       &#064;Version protected Integer version;
 *       protected String address;
 *       public Integer getId() { ... }
 *       public void setId(Integer id) { ... }
 *       public String getAddress() { ... }
 *       public void setAddress(String address) { ... }
 *   }
 *
 *   &#064;Entity
 *   &#064;AttributeOverride(name="address", column=&#064;Column(name="ADDR"))
 *   public class PartTimeEmployee extends Employee {
 *       // address field mapping overridden to ADDR
 *       protected Float wage();
 *       public Float getHourlyWage() { ... }
 *       public void setHourlyWage(Float wage) { ... }
 *   }
 * </pre>
 *
 * @see Embedded
 * @see Embeddable
 * @see MappedSuperclass
 *
 * @since Java Persistence 1.0
 */
@Target({TYPE, METHOD, FIELD}) 
@Retention(RUNTIME)

public @interface AttributeOverride {

    /**
     * (Required) The name of the property whose mapping is being 
     * overridden if property-based access is being used, or the 
     * name of the field if field-based access is used.
     */
    String name();

    /**
     * (Required) The column that is being mapped to the persistent 
     * attribute. The mapping type will remain the same as is 
     * defined in the embeddable class or mapped superclass.
     */
    Column column();
}
