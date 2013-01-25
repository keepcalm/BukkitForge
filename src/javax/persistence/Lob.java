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
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Specifies that a persistent property or field should be persisted 
 * as a large object to a database-supported large object type. 
 * The <code>Lob</code> annotation may be used in conjunction 
 * with the {@link Basic} annotation. A <code>Lob</code> may be 
 * either a binary or character type. 
 *
 * <p> The <code>Lob</code> type is inferred from the type of the 
 * persistent field or property, and except for string and 
 * character-based types defaults to Blob.
 * <pre>
 *
 *   Example:
 *
 *   &#064;Lob &#064;Basic(fetch=LAZY)
 *   &#064;Column(name="REPORT")
 *   protected String report;
 * </pre>
 *
 * @since Java Persistence 1.0
 */
@Target({METHOD, FIELD}) 
@Retention(RUNTIME)
public @interface Lob {
}
