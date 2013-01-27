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
 * This annotation specifies a primary key column that is used 
 * as a foreign key to join to another table. 
 *
 * <p> It is used to join the primary table of an entity subclass 
 * in the {@link InheritanceType#JOINED JOINED} mapping strategy 
 * to the primary table of its superclass; it is used within a 
 * {@link SecondaryTable} annotation to join a secondary table 
 * to a primary table; and it may be used in a {@link OneToOne} 
 * mapping in which the primary key of the referencing entity 
 * is used as a foreign key to the referenced entity. 
 *
 * <p> If no <code>PrimaryKeyJoinColumn</code> annotation is 
 * specified for a subclass in the {@link InheritanceType#JOINED 
 * JOINED} mapping strategy, the foreign key columns are assumed 
 * to have the same names as the primary key columns of the 
 * primary table of the superclass
 *
 * <pre>
 *
 *    Example: Customer and ValuedCustomer subclass
 *
 *    &#064;Entity
 *    &#064;Table(name="CUST")
 *    &#064;Inheritance(strategy=JOINED)
 *    &#064;DiscriminatorValue("CUST")
 *    public class Customer { ... }
 *    
 *    &#064;Entity
 *    &#064;Table(name="VCUST")
 *    &#064;DiscriminatorValue("VCUST")
 *    &#064;PrimaryKeyJoinColumn(name="CUST_ID")
 *    public class ValuedCustomer extends Customer { ... }
 * </pre>
 *
 * @since Java Persistence 1.0
 */
@Target({TYPE, METHOD, FIELD})
@Retention(RUNTIME)

public @interface PrimaryKeyJoinColumn {

    /** 
     * The name of the primary key column of the current table.
     * <p> Defaults to the same name as the primary key column 
     * of the primary table of the superclass ({@link 
     * InheritanceType#JOINED JOINED} mapping strategy); the same 
     * name as the primary key column of the primary table 
     * ({@link SecondaryTable} mapping); or the same name as the 
     * primary key column for the table for the referencing entity 
     * ({@link OneToOne} mapping)
     */
    String name() default "";

    /** 
     * (Optional) The name of the primary key column of the table 
     * being joined to.
     * <p> Defaults to the same name as the primary key column 
     * of the primary table of the superclass ({@link 
     * InheritanceType#JOINED JOINED} mapping strategy); the same 
     * name as the primary key column of the primary table 
     * ({@link SecondaryTable} mapping); or the same name as the 
     * primary key column for the table for the referencing entity 
     * ({@link OneToOne} mapping)
     */
    String referencedColumnName() default "";

    /**
     * (Optional) The SQL fragment that is used when generating the 
     * DDL for the column. This should not be specified for a 
     * {@link OneToOne} primary key association.
     * <p> Defaults to the generated SQL to create a column of the 
     * inferred type.
     */
    String columnDefinition() default "";
}
