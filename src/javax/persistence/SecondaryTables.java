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
 * This annotation is used to specify multiple secondary tables 
 * for an entity.
 *
 * <pre>
 *    Example 1: Multiple secondary tables assuming primary key columns are named the same in all tables.
 *
 *    &#064;Entity
 *    &#064;Table(name="EMPLOYEE")
 *    &#064;SecondaryTables({
 *        &#064;SecondaryTable(name="EMP_DETAIL"),
 *        &#064;SecondaryTable(name="EMP_HIST")
 *    })
 *    public class Employee { ... }
 *    
 *    Example 2: Multiple secondary tables with differently named primary key columns. 
 *
 *    &#064;Entity
 *    &#064;Table(name="EMPLOYEE")
 *    &#064;SecondaryTables({
 *        &#064;SecondaryTable(name="EMP_DETAIL", 
 *            pkJoinColumns=&#064;PrimaryKeyJoinColumn(name="EMPL_ID")),
 *        &#064;SecondaryTable(name="EMP_HIST", 
 *            pkJoinColumns=&#064;PrimaryKeyJoinColumn(name="EMPLOYEE_ID"))
 *    })
 *    public class Employee { ... }
 * </pre>
 *
 * @since Java Persistence 1.0
 */
@Target(TYPE) 
@Retention(RUNTIME)

public @interface SecondaryTables {

    /** The secondary tables for an entity. */
    SecondaryTable[] value();
}
