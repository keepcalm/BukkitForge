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
 * Is used to specify the value of the discriminator column for 
 * entities of the given type. The <code>DiscriminatorValue</code> 
 * annotation can only be specified on a concrete entity 
 * class. If the <code>DiscriminatorValue</code> annotation is not 
 * specified and a discriminator column is used, a provider-specific 
 * function will be used to generate a value representing the 
 * entity type.  If the {@link DiscriminatorType} is {@link 
 * DiscriminatorType#STRING STRING}, the discriminator value 
 * default is the entity name. 
 *
 * <p> The inheritance strategy and the discriminator column 
 * are only specified in the root of an entity class hierarchy 
 * or subhierarchy in which a different inheritance strategy is 
 * applied. The discriminator value, if not defaulted, should be 
 * specified for each entity class in the hierarchy.
 *
 * <pre>
 *
 *    Example:
 *
 *    &#064;Entity
 *    &#064;Table(name="CUST")
 *    &#064;Inheritance(strategy=SINGLE_TABLE)
 *    &#064;DiscriminatorColumn(name="DISC", discriminatorType=STRING,length=20)
 *    &#064;DiscriminatorValue("CUSTOMER")
 *    public class Customer { ... }
 *
 *    &#064;Entity
 *    &#064;DiscriminatorValue("VCUSTOMER")
 *    public class ValuedCustomer extends Customer { ... }
 * </pre>
 *
 * @since Java Persistence 1.0
 */
@Target({TYPE}) 
@Retention(RUNTIME)

public @interface DiscriminatorValue {

    /**
     * (Optional) The value that indicates that the
     * row is an entity of the annotated entity type.
     *
     * <p> If the <code>DiscriminatorValue</code> annotation is not 
     * specified and a discriminator column is used, a provider-specific 
     * function will be used to generate a value representing the 
     * entity type.  If the DiscriminatorType is {@link 
     * DiscriminatorType#STRING STRING}, the discriminator value 
     * default is the entity name. 
     */
    String value();
}
