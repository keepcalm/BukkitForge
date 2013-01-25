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
 * The <code>EntityTransaction</code> interface is used to control 
 * resource transactions on resource-local entity managers. The 
 * {@link EntityManager#getTransaction EntityManager.getTransaction()} 
 * method returns the <code>EntityTransaction</code> interface.
 *
 * @since Java Persistence 1.0
 */
public interface EntityTransaction {
    /**
     * Start the resource transaction.
     * @throws IllegalStateException if {@link #isActive()} is true.
     */
    public void begin();
 
    /**
     * Commit the current transaction, writing any unflushed
     * changes to the database.
     * @throws IllegalStateException if {@link #isActive()} is false.
     * @throws RollbackException if the commit fails.
     */
    public void commit();
 
    /**
     * Roll back the current transaction
     * @throws IllegalStateException if {@link #isActive()} is false.
     * @throws PersistenceException if an unexpected error
     * condition is encountered.
     */
    public void rollback();

    /**
     * Mark the current transaction so that the only possible
     * outcome of the transaction is for the transaction to be
     * rolled back.
     * @throws IllegalStateException if {@link #isActive()} is false.
     */
    public void setRollbackOnly();

    /**
     * Determine whether the current transaction has been marked
     * for rollback.
     * @throws IllegalStateException if {@link #isActive()} is false.
     */
    public boolean getRollbackOnly();
 
    /**
     * Indicate whether a transaction is in progress.
     * @throws PersistenceException if an unexpected error
     * condition is encountered.
     */
    public boolean isActive();
}
