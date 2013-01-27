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
import static javax.persistence.FetchType.LAZY;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Defines a many-valued association with one-to-many multiplicity.
 * 
 * <p> If the collection is defined using generics to specify the 
 * element type, the associated target entity type need not be 
 * specified; otherwise the target entity class must be specified.
 *
 * <pre>
 *
 *    Example 1: One-to-Many association using generics
 *
 *    In Customer class:
 *
 *    &#064;OneToMany(cascade=ALL, mappedBy="customer")
 *    public Set<Order> getOrders() { return orders; }
 *
 *    In Order class:
 *
 *    &#064;ManyToOne
 *    &#064;JoinColumn(name="CUST_ID", nullable=false)
 *    public Customer getCustomer() { return customer; }
 *
 *    Example 2: One-to-Many association without using generics
 *
 *    In Customer class:
 *
 *    &#064;OneToMany(targetEntity=com.acme.Order.class, cascade=ALL,
 *            mappedBy="customer")
 *    public Set getOrders() { return orders; }
 *
 *    In Order class:
 *
 *    &#064;ManyToOne
 *    &#064;JoinColumn(name="CUST_ID", nullable=false)
 *    public Customer getCustomer() { return customer; }
 * </pre>
 *
 * @since Java Persistence 1.0
 */
@Target({METHOD, FIELD}) 
@Retention(RUNTIME)

public @interface OneToMany {

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
