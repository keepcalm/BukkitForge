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
 * This annotation specifies the primary table for the annotated 
 * entity. Additional tables may be specified using {@link SecondaryTable}
 * or {@link SecondaryTables} annotation. 
 *
 * <p> If no <code>Table</code> annotation is specified for an entity 
 * class, the default values apply.
 *
 * <pre>
 *    Example:
 *
 *    &#064;Entity
 *    &#064;Table(name="CUST", schema="RECORDS")
 *    public class Customer { ... }
 * </pre>
 *
 * @since Java Persistence 1.0
 */
@Target(TYPE) 
@Retention(RUNTIME)

public @interface Table {

    /**
     * (Optional) The name of the table.
     * <p> Defaults to the entity name.
     */
    String name() default "";

    /** (Optional) The catalog of the table.
     * <p> Defaults to the default catalog.
     */
    String catalog() default "";

    /** (Optional) The schema of the table.
     * <p> Defaults to the default schema for user.
     */
    String schema() default "";

    /**
     * (Optional) Unique constraints that are to be placed on 
     * the table. These are only used if table generation is in 
     * effect. These constraints apply in addition to any constraints 
     * specified by the {@link Column} and {@link JoinColumn} 
     * annotations and constraints entailed by primary key mappings.
     * <p> Defaults to no additional constraints.
     */
    UniqueConstraint[] uniqueConstraints() default {};
}
