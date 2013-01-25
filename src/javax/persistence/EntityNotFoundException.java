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
 * Thrown by the persistence provider when an entity reference obtained by
 * {@link EntityManager#getReference EntityManager.getReference(Class,Object)}
 * is accessed but the entity does not exist. Also thrown when 
 * {@link EntityManager#refresh EntityManager.refresh(Object)} is called 
 * and the object no longer exists in the database. The 
 * current transaction, if one is active, will be marked for rollback.
 *
 * @see javax.persistence.EntityManager#getReference(Class,Object)
 * @see javax.persistence.EntityManager#refresh(Object)
 *
 * @since Java Persistence 1.0
 */
public class EntityNotFoundException extends PersistenceException {
	
        /**
         * Constructs a new <code>EntityNotFoundException</code> exception
         * with <code>null</code> as its detail message.
         */
	public EntityNotFoundException() {
		super();
	}

        /**
         * Constructs a new <code>EntityNotFoundException</code> exception
         * with the specified detail message.
         * @param   message   the detail message.
         */
	public EntityNotFoundException(String message) {
		super(message);
	}

};
