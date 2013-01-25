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
 * Defines inheritance strategy options.
 *
 * @since Java Persistence 1.0
 */
public enum InheritanceType { 

    /** A single table per class hierarchy */
    SINGLE_TABLE, 

    /** A table per concrete entity class */
    TABLE_PER_CLASS, 

    /** 
     * A strategy in which fields that are specific to a 
     * subclass are mapped to a separate table than the fields 
     * that are common to the parent class, and a join is 
     * performed to instantiate the subclass.
     */
    JOINED 
}
