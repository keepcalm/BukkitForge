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
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * This annotation specifies the ordering of the elements of a 
 * collection valued association at the point when the association 
 * is retrieved.
 * 
 * <p> The syntax of the <code>value</code> ordering element is an 
 * <code>orderby_list</code>, as follows:
 * 
 * <pre>
 *    orderby_list::= orderby_item [,orderby_item]*
 *    orderby_item::= property_or_field_name [ASC | DESC]
 * </pre>
 * 
 * <p> If <code>ASC</code> or <code>DESC</code> is not specified, 
 * <code>ASC</code> (ascending order) is assumed.
 *
 * <p> If the ordering element is not specified, ordering by 
 * the primary key of the associated entity is assumed.
 *
 * <p> The property or field name must correspond to that of a 
 * persistent property or field of the associated class. The 
 * properties or fields used in the ordering must correspond to 
 * columns for which comparison operators are supported.
 *
 * <pre>
 *    Example:
 *    
 *    &#064;Entity public class Course {
 *     ...
 *     &#064;ManyToMany
 *     &#064;OrderBy("lastname ASC")
 *     public List<Student> getStudents() {...};
 *     ...
 *    }
 *    
 *    &#064;Entity public class Student {
 *      ...
 *      &#064;ManyToMany(mappedBy="students")
 *      &#064;OrderBy // PK is assumed
 *      public List<Course> getCourses() {...};
 *      ...
 *    }
 * </pre>
 *
 * @since Java Persistence 1.0
 */
@Target({METHOD, FIELD}) 
@Retention(RUNTIME)

public @interface OrderBy {

    /**
    * An <code>orderby_list</code>, specified as follows:
    *
    * <pre>
    *    orderby_list::= orderby_item [,orderby_item]*
    *    orderby_item::= property_or_field_name [ASC | DESC]
    * </pre>
    *
    * <p> If <code>ASC</code> or <code>DESC</code> is not specified,
    * <code>ASC</code> (ascending order) is assumed.
    *
    * <p> If the ordering element is not specified, ordering by
    * the primary key of the associated entity is assumed.
    */
    String value() default "";
}
