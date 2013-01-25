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

import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Interface used to control query execution.
 *
 * @since Java Persistence 1.0
 */
public interface Query {

    /**
     * Execute a SELECT query and return the query results
     * as a List.
     * @return a list of the results
     * @throws IllegalStateException if called for a Java 
     *    Persistence query language UPDATE or DELETE statement
     */   
    public List getResultList();

    /**
     * Execute a SELECT query that returns a single result.
     * @return the result
     * @throws EntityNotFoundException if there is no result
     * @throws NonUniqueResultException if more than one result
     * @throws IllegalStateException if called for a Java 
     *    Persistence query language UPDATE or DELETE statement
     */
    public Object getSingleResult();

    /**
     * Execute an update or delete statement.
     * @return the number of entities updated or deleted
     * @throws IllegalStateException if called for a Java 
     *    Persistence query language SELECT statement
     * @throws TransactionRequiredException if there is
     *    no transaction
     */
    public int executeUpdate();

    /**
     * Set the maximum number of results to retrieve.
     * @param maxResult
     * @return the same query instance
     * @throws IllegalArgumentException if argument is negative
     */
    public Query setMaxResults(int maxResult);

    /**
     * Set the position of the first result to retrieve.
     * @param startPosition the start position of the first result, numbered from 0
     * @return the same query instance
     * @throws IllegalArgumentException if argument is negative
     */
    public Query setFirstResult(int startPosition);

    /**
     * Set an implementation-specific hint.
     * If the hint name is not recognized, it is silently ignored.
     * @param hintName
     * @param value
     * @return the same query instance
     * @throws IllegalArgumentException if the second argument is not
     *    valid for the implementation
     */
    public Query setHint(String hintName, Object value);

    /**
     * Bind an argument to a named parameter.
     * @param name the parameter name
     * @param value
     * @return the same query instance
     * @throws IllegalArgumentException if parameter name does not
     *    correspond to parameter in query string
     *    or argument is of incorrect type
     */
    public Query setParameter(String name, Object value);

    /**
     * Bind an instance of java.util.Date to a named parameter.
     * @param name
     * @param value
     * @param temporalType
     * @return the same query instance
     * @throws IllegalArgumentException if parameter name does not
     *    correspond to parameter in query string
     */
    public Query setParameter(String name, Date value, TemporalType temporalType);

    /**
     * Bind an instance of java.util.Calendar to a named parameter.
     * @param name
     * @param value
     * @param temporalType
     * @return the same query instance
     * @throws IllegalArgumentException if parameter name does not
     *    correspond to parameter in query string
     */
    public Query setParameter(String name, Calendar value, TemporalType temporalType);

    /**
     * Bind an argument to a positional parameter.
     * @param position
     * @param value
     * @return the same query instance
     * @throws IllegalArgumentException if position does not
     *    correspond to positional parameter of query
     *    or argument is of incorrect type
     */
    public Query setParameter(int position, Object value);

    /**
     * Bind an instance of java.util.Date to a positional parameter.
     * @param position
     * @param value
     * @param temporalType
     * @return the same query instance
     * @throws IllegalArgumentException if position does not
     *    correspond to positional parameter of query
     */
    public Query setParameter(int position, Date value, TemporalType temporalType);

    /**
     * Bind an instance of java.util.Calendar to a positional parameter.
     * @param position
     * @param value
     * @param temporalType
     * @return the same query instance
     * @throws IllegalArgumentException if position does not
     *    correspond to positional parameter of query
     */
    public Query setParameter(int position, Calendar value, TemporalType temporalType);

    /**
     * Set the flush mode type to be used for the query execution.
     * The flush mode type applies to the query regardless of the
     * flush mode type in use for the entity manager.
     * @param flushMode
     */
    public Query setFlushMode(FlushModeType flushMode);
}
