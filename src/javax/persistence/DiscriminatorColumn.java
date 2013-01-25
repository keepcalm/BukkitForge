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
import static javax.persistence.DiscriminatorType.STRING;

/**
 * Is used to define the discriminator column for the 
 * {@link InheritanceType#SINGLE_TABLE SINGLE_TABLE} and 
 * {@link InheritanceType#JOINED JOINED} inheritance mapping strategies.
 * 
 * <p> The strategy and the discriminator column are only 
 * specified in the root of an entity class hierarchy or
 * subhierarchy in which a different inheritance strategy is applied
 * 
 * <p> If the <code>DiscriminatorColumn</code> annotation is missing, 
 * and a discriminator column is required, the name of the 
 * discriminator column defaults to <code>"DTYPE"</code> and the discriminator 
 * type to {@link DiscriminatorType#STRING DiscriminatorType.STRING}.
 *
 * <pre>
 *     Example:
 *     &#064;Entity
 *     &#064;Table(name="CUST")
 *     &#064;Inheritance(strategy=SINGLE_TABLE)
 *     &#064;DiscriminatorColumn(name="DISC", discriminatorType=STRING,length=20)
 *     public class Customer { ... }
 *
 *     &#064;Entity
 *     public class ValuedCustomer extends Customer { ... }
 * </pre>
 *
 * @since Java Persistence 1.0
 */
@Target({TYPE}) 
@Retention(RUNTIME)

public @interface DiscriminatorColumn {

    /**
     * (Optional) The name of column to be used for the discriminator.
     */
    String name() default "DTYPE";

    /**
     * (Optional) The type of object/column to use as a class discriminator.
     * Defaults to {@link DiscriminatorType#STRING DiscriminatorType.STRING}.
     */
    DiscriminatorType discriminatorType() default STRING;

    /**
     * (Optional) The SQL fragment that is used when generating the DDL 
     * for the discriminator column.
     * <p> Defaults to the provider-generated SQL to create a column 
     * of the specified discriminator type.
     */
    String columnDefinition() default "";

    /** 
     * (Optional) The column length for String-based discriminator types. 
     * Ignored for other discriminator types.
     */
    int length() default 31;
}
