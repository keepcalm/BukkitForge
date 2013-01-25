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
 * Flush mode setting.
 *
 * <p> When queries are executed within a transaction, if 
 * <code>FlushModeType.AUTO</code> is set on the {@link Query} 
 * object, or if the flush mode setting for the persistence context 
 * is <code>AUTO</code> (the default) and a flush mode setting has 
 * not been specified for the {@link Query} object, the persistence 
 * provider is responsible for ensuring that all updates to the state 
 * of all entities in the persistence context which could potentially 
 * affect the result of the query are visible to the processing 
 * of the query. The persistence provider implementation may achieve 
 * this by flushing those entities to the database or by some other 
 * means. If <code>FlushModeType.COMMIT</code> is set, the effect 
 * of updates made to entities in the persistence context upon 
 * queries is unspecified.
 *
 * <p> If there is no transaction active, the persistence provider 
 * must not flush to the database.
 *
 * @since Java Persistence 1.0
 */
public enum FlushModeType {

    /** Flushing must occur only at transaction commit */
    COMMIT,

    /** (Default) Flushing to occur at query execution */
    AUTO
}
