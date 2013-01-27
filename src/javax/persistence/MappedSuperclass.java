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
 * Designates a class whose mapping information is applied 
 * to the entities that inherit from it. A mapped superclass 
 * has no separate table defined for it.  
 *
 * <p> A class designated with the <code>MappedSuperclass</code> 
 * annotation can be mapped in the same way as an entity except that the 
 * mappings will apply only to its subclasses since no table 
 * exists for the mapped superclass itself. When applied to the 
 * subclasses the inherited mappings will apply in the context 
 * of the subclass tables. Mapping information may be overridden 
 * in such subclasses by using the {@link AttributeOverride} and 
 * {@link AssociationOverride} annotations or corresponding XML elements.
 *
 * <pre>
 *    Example: Concrete class as a mapped superclass
 *
 *    &#064;MappedSuperclass
 *    public class Employee {
 *    
 *        &#064;Id protected Integer empId;
 *        &#064;Version protected Integer version;
 *        &#064;ManyToOne &#064;JoinColumn(name="ADDR")
 *        protected Address address;
 *    
 *        public Integer getEmpId() { ... }
 *        public void setEmpId(Integer id) { ... }
 *        public Address getAddress() { ... }
 *        public void setAddress(Address addr) { ... }
 *    }
 *    
 *    // Default table is FTEMPLOYEE table
 *    &#064;Entity
 *    public class FTEmployee extends Employee {
 *    
 *        // Inherited empId field mapped to FTEMPLOYEE.EMPID
 *        // Inherited version field mapped to FTEMPLOYEE.VERSION
 *        // Inherited address field mapped to FTEMPLOYEE.ADDR fk
 *        
 *    
 *    // Defaults to FTEMPLOYEE.SALARY
 *    
 *    protected Integer salary;
 *    
 *    
 *    public FTEmployee() {}
 *    
 *    
 *    public Integer getSalary() { ... }
 *    
 *    public void setSalary(Integer salary) { ... }
 *    }
 *    
 *    &#064;Entity &#064;Table(name="PT_EMP")
 *    &#064;AssociationOverride(name="address", 
 *    
 *    
 *    joincolumns=&#064;JoinColumn(name="ADDR_ID"))
 *    public class PartTimeEmployee extends Employee {
 *    
 *        // Inherited empId field mapped to PT_EMP.EMPID
 *        // Inherited version field mapped to PT_EMP.VERSION
 *        // address field mapping overridden to PT_EMP.ADDR_ID fk
 *        &#064;Column(name="WAGE")
 *        protected Float hourlyWage;
 *    
 *        public PartTimeEmployee() {}
 *    
 *        public Float getHourlyWage() { ... }
 *        public void setHourlyWage(Float wage) { ... }
 *    }
 *
 *    Example: Non-entity superclass
 *
 *    public class Cart {
 *    
 *        // This state is transient
 *        Integer operationCount;
 *    
 *        public Cart() { operationCount = 0; }
 *        public Integer getOperationCount() { return operationCount; }
 *        public void incrementOperationCount() { operationCount++; }
 *    }
 *    
 *    &#064Entity
 *    public class ShoppingCart extends Cart {
 *    
 *        Collection<Item> items = new Vector<Item>();
 *    
 *        public ShoppingCart() { super(); }
 *    
 *    
 *    ...
 *    
 *        &#064OneToMany
 *        public Collection<Item> getItems() { return items; }
 *        public void addItem(Item item) {
 *            items.add(item);
 *            incrementOperationCount();
 *        }
 *    }
 * </pre>
 *
 * @since Java Persistence 1.0
 */
@Target({TYPE})
@Retention(RUNTIME)

public @interface MappedSuperclass {
}
