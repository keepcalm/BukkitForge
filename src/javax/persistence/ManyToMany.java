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
import javax.persistence.CascadeType;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import static javax.persistence.FetchType.LAZY;

/**
 * Defines a many-valued association with many-to-many multiplicity. 
 * If the Collection is defined using generics to specify the element 
 * type, the associated target entity class does not need to be 
 * specified; otherwise it must be specified.
 *
 * <p> Every many-to-many association has two sides, the owning 
 * side and the non-owning, or inverse, side.  The join table is 
 * specified on the owning side. If the association is bidirectional, 
 * either side may be designated as the owning side.
 *
 * <p> The same annotation elements for the {@link OneToMany} 
 * annotation apply to the <code>ManyToMany</code> annotation. 
 *
 * <pre>
 *
 *    Example 1:
 *
 *    In Customer class:
 *
 *    &#064;ManyToMany
 *    &#064;JoinTable(name="CUST_PHONES")
 *    public Set<PhoneNumber> getPhones() { return phones; }
 *
 *    In PhoneNumber class:
 *
 *    &#064;ManyToMany(mappedBy="phones")
 *    public Set<Customer> getCustomers() { return customers; }
 *
 *    Example 2:
 *
 *    In Customer class:
 *
 *    &#064;ManyToMany(targetEntity=com.acme.PhoneNumber.class)
 *    public Set getPhones() { return phones; }
 *
 *    In PhoneNumber class:
 *
 *    &#064;ManyToMany(targetEntity=com.acme.Customer.class, mappedBy="phones")
 *    public Set getCustomers() { return customers; }
 *
 *    Example 3:
 *
 *    In Customer class:
 *
 *    &#064;ManyToMany
 *    &#064;JoinTable(name="CUST_PHONE",
 *        joinColumns=
 *            &#064;JoinColumn(name="CUST_ID", referencedColumnName="ID"),
 *        inverseJoinColumns=
 *            &#064;JoinColumn(name="PHONE_ID", referencedColumnName="ID")
 *        )
 *    public Set<PhoneNumber> getPhones() { return phones; }
 *
 *    In PhoneNumberClass:
 *
 *    &#064;ManyToMany(mappedBy="phones")
 *    public Set<Customer> getCustomers() { return customers; }
 * </pre>
 *
 * @since Java Persistence 1.0
 */
@Target({METHOD, FIELD}) 
@Retention(RUNTIME)

public @interface ManyToMany {

    /**
     * (Optional) The entity class that is the target
     * of the association. Optional only if the collection
     * property is defined using Java generics.
     * Must be specified otherwise.
     *
     * <p> Defaults to the parameterized type of
     * the collection when defined using generics.
     */
    Class targetEntity() default void.class;

    /** 
     * (Optional) The operations that must be cascaded to 
     * the target of the association.
     * <p> Defaults to no operations being cascaded.
     */
    CascadeType[] cascade() default {};

    /** (Optional) Whether the association should be
     * lazily loaded or must be eagerly fetched. The
     * {@link FetchType#EAGER EAGER} strategy is a 
     * requirement on the persistenceprovider runtime 
     * that the associatedentities must be eagerly fetched. 
     * The {@link FetchType#LAZY LAZY} strategy is a hint 
     * to the persistence provider runtime.
     */
    FetchType fetch() default LAZY;

    /** 
     * The field that owns the relationship. Required unless 
     * the relationship is unidirectional.
     */
    String mappedBy() default "";
}
