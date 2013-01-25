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
 * Lock modes that can be specified by means of the 
 * {@link EntityManager#lock EntityManager.lock()} method.
 * 
 * <p> The semantics of requesting locks of type 
 * {@link LockModeType#READ LockModeType.READ} and {@link 
 * LockModeType#WRITE LockModeType.WRITE} are the following.
 *
 * <p> If transaction T1 calls lock(entity, {@link 
 * LockModeType#READ LockModeType.READ}) on a versioned object, 
 * the entity manager must ensure that neither of the following 
 * phenomena can occur:
 * <ul>
 *   <li> P1 (Dirty read): Transaction T1 modifies a row. 
 * Another transaction T2 then reads that row and obtains 
 * the modified value, before T1 has committed or rolled back. 
 * Transaction T2 eventually commits successfully; it does not 
 * matter whether T1 commits or rolls back and whether it does 
 * so before or after T2 commits.
 *   <li>
 *   </li> P2 (Non-repeatable read): Transaction T1 reads a row. 
 * Another transaction T2 then modifies or deletes that row, 
 * before T1 has committed. Both transactions eventually commit 
 * successfully.
 *   </li>
 * </ul>
 *
 * <p> Lock modes must always prevent the phenomena P1 and P2.
 *
 * <p> In addition, calling lock(entity, LockModeType.WRITE) on 
 * a versioned object, will also force an update (increment) to 
 * the entity's version column.
 *
 * <p> The persistence implementation is not required to support 
 * calling {@link EntityManager#lock EntityManager.lock()} on a 
 * non-versioned object. When it cannot support a such lock call, 
 * it must throw the {@link PersistenceException}.
 *
 *
 * @since Java Persistence 1.0
 */
public enum LockModeType  {

    /** Read lock */
    READ,

    /** Write lock */
    WRITE
}
