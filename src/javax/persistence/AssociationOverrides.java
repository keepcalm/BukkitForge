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
 * This annotation is used to override mappings of multiple 
 * many-to-one or one-to-one relationship properties or fields.
 *
 * <pre>
 *    
 *    Example:
 *    &#064;MappedSuperclass
 *    public class Employee {
 *    
 *        &#064;Id protected Integer id;
 *        &#064;Version protected Integer version;
 *        &#064;ManyToOne protected Address address;
 *        &#064;OneToOne protected Locker locker;
 *    
 *        public Integer getId() { ... }
 *        public void setId(Integer id) { ... }
 *        public Address getAddress() { ... }
 *        public void setAddress(Address address) { ... }
 *        public Locker getLocker() { ... }
 *        public void setLocker(Locker locker) { ... }
 *    
 *    }
 *    
 *    &#064;Entity
 *    &#064;AssociationOverrides({
 *    
 *    &#064;AssociationOverride(name="address", 
 *            joinColumns=&#064;JoinColumn("ADDR_ID")),
 *        &#064;AttributeOverride(name="locker", 
 *            joinColumns=&#064;JoinColumn("LCKR_ID"))})
 *    public PartTimeEmployee { ... }
 * </pre>
 *
 * @since Java Persistence 1.0
 */
@Target({TYPE, METHOD, FIELD}) 
@Retention(RUNTIME)

public @interface AssociationOverrides {

    /** Mapping overrides of relationship properties or fields */
    AssociationOverride[] value();
}
