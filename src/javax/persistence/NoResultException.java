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
 * Thrown by the persistence provider when {@link Query#getSingleResult 
 * getSingleResult()} is executed on a query
 * and there is no result to return. This exception will not cause the current 
 * transaction, if one is active, to be marked for roll back.
 *
 * @see javax.persistence.Query#getSingleResult()
 *
 * @since Java Persistence 1.0
 */
public class NoResultException extends PersistenceException {

        /** 
         * Constructs a new <code>NoResultException</code> exception 
         * with <code>null</code> as its detail message.
         */
	public NoResultException() {
		super();
	}

        /** 
         * Constructs a new <code>NoResultException</code> exception 
         * with the specified detail message.
         * @param   message   the detail message.
         */
	public NoResultException(String message) {
		super(message);
	}
};

