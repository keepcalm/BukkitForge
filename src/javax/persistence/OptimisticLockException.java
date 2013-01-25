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
 * Thrown by the persistence provider when an optimistic locking conflict
 * occurs. This exception may be thrown as part of an API call, a flush or at 
 * commit time. The current transaction, if one is active, will be marked for
 * rollback.
 *
 * @since Java Persistence 1.0
 */
public class OptimisticLockException extends PersistenceException {
    
    /** The object that caused the exception */
    Object entity;

    /** 
     * Constructs a new <code>OptimisticLockException</code> exception 
     * with <code>null</code> as its detail message.
     */
    public OptimisticLockException() {
        super();
    }

    /** 
     * Constructs a new <code>OptimisticLockException</code> exception 
     * with the specified detail message.
     * @param   message   the detail message.
     */
    public OptimisticLockException(String message) {
        super(message);
    }

    /** 
     * Constructs a new <code>OptimisticLockException</code> exception 
     * with the specified detail message and cause.
     * @param   message   the detail message.
     * @param   cause     the cause.
     */
    public OptimisticLockException(String message, Throwable cause) {
        super(message, cause);
    }

    /** 
     * Constructs a new <code>OptimisticLockException</code> exception 
     * with the specified cause.
     * @param   cause     the cause.
     */
    public OptimisticLockException(Throwable cause) {
        super(cause);
    }

    /** 
     * Constructs a new <code>OptimisticLockException</code> exception 
     * with the specified entity.
     * @param   entity     the entity.
     */
    public OptimisticLockException(Object entity) {
        this.entity = entity;
    }

    /** 
     * Constructs a new <code>OptimisticLockException</code> exception 
     * with the specified detail message, cause, and entity.
     * @param   message   the detail message.
     * @param   cause     the cause.
     * @param   entity     the entity.
     */
    public OptimisticLockException(String message, Throwable cause, Object entity) {
        super(message, cause);
        this.entity = entity;
    }
    
    /**
     * Returns the entity that caused this exception.
     * @return the entity.
     */
    public Object getEntity() {
        return this.entity;
    }

};

