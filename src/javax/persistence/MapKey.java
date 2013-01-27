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
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Is used to specify the map key for associations of type 
 * {@link java.util.Map}.
 * 
 * <p> If a persistent field or property other than the primary 
 * key is used as a map key then it is expected to have a 
 * uniqueness constraint associated with it.
 *
 * <pre>
 *
 *    Example 1:
 *
 *    &#064;Entity
 *    public class Department {
 *        ...
 *        &#064;OneToMany(mappedBy="department")
 *        &#064;MapKey(name="empId")
 *        public Map<Integer, Employee> getEmployees() {... }
 *        ...
 *    }
 *
 *    &#064;Entity
 *    public class Employee {
 *        ...
 *        &#064;Id Integer getEmpid() { ... }
 *        &#064;ManyToOne
 *        &#064;JoinColumn(name="dept_id")
 *        public Department getDepartment() { ... }
 *        ...
 *    }
 *
 *    Example 2:
 *
 *    &#064;Entity
 *        public class Department {
 *        ...
 *        &#064;OneToMany(mappedBy="department")
 *        &#064;MapKey(name="empPK")
 *        public Map<EmployeePK, Employee> getEmployees() {... }
 *        ...
 *    }
 *
 *    &#064;Entity
 *        public class Employee {
 *        &#064;EmbeddedId public EmployeePK getEmpPK() { ... }
 *        ...
 *        &#064;ManyToOne
 *        &#064;JoinColumn(name="dept_id")
 *        public Department getDepartment() { ... }
 *        ...
 *    }
 *
 *    &#064;Embeddable
 *    public class EmployeePK {
 *        String name;
 *        Date bday;
 *    }
 * </pre>
 *
 * @since Java Persistence 1.0
 */
@Target({METHOD, FIELD}) 
@Retention(RUNTIME)

public @interface MapKey {

    /**
     * The name of the persistent field or property of the 
     * associated entity that is used as the map key. If the 
     * name element is not specified, the primary key of the 
     * associated entity is used as the map key. If the 
     * primary key is a composite primary key and is mapped 
     * as {@link IdClass}, an instance of the primary key 
     * class is used as the key.
     */
    String name() default "";
}
