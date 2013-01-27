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
 * Defines a class whose instances are stored as an intrinsic 
 * part of an owning entity and share the identity of the entity. 
 * Each of the persistent properties or fields of the embedded 
 * object is mapped to the database table for the entity. Only 
 * {@link Basic}, {@link Column}, {@link Lob}, 
 * {@link Temporal}, and {@link Enumerated} mapping 
 * annotations may portably be used to map the persistent fields 
 * or properties of classes annotated as {@link Embeddable}.
 *
 * <p> Note that the {@link Transient} annotation may be used to 
 * designate the non-persistent state of an embeddable class.
 *
 * @since Java Persistence 1.0
 */
@Target({TYPE}) 
@Retention(RUNTIME)

public @interface Embeddable {
}
