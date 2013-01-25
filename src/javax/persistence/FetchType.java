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

/**
 * Defines strategies for fetching data from the database.
 * The <code>EAGER</code> strategy is a requirement on the persistence 
 * provider runtime that data must be eagerly fetched. The 
 * <code>LAZY</code> strategy is a hint to the persistence provider 
 * runtime that data should be fetched lazily when it is 
 * first accessed. The implementation is permitted to eagerly 
 * fetch data for which the <code>LAZY</code> strategy hint has been 
 * specified. In particular, lazy fetching might only be 
 * available for {@link Basic} mappings for which property-based 
 * access is used.
 *
 * <pre>
 *   Example:
 *   &#064;Basic(fetch=LAZY)
 *   protected String getName() { return name; }
 * </pre>
 *
 * @since Java Persistence 1.0
 */
public enum FetchType {

    /** Defines that data can be lazily fetched */
    LAZY,

    /** Defines that data must be eagerly fetched */
    EAGER
}
