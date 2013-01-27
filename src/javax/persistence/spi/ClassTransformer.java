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
package javax.persistence.spi;

import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;
/**
 * A persistence provider supplies an instance of this
 * interface to the {@link PersistenceUnitInfo#addTransformer 
 * PersistenceUnitInfo.addTransformer}
 * method. The supplied transformer instance will get
 * called to transform entity class files when they are
 * loaded or redefined. The transformation occurs before
 * the class is defined by the JVM.
 *
 * @since Java Persistence 1.0
 */
public interface ClassTransformer {
	/**
	* Invoked when a class is being loaded or redefined.
	* The implementation of this method may transform the
	* supplied class file and return a new replacement class
	* file.
	*
	* @param loader The defining loader of the class to be
	* transformed, may be null if the bootstrap loader
	* @param className The name of the class in the internal form
	* of fully qualified class and interface names
	* @param classBeingRedefined If this is a redefine, the
	* class being redefined, otherwise null
	* @param protectionDomain The protection domain of the
	* class being defined or redefined
	* @param classfileBuffer The input byte buffer in class
	* file format - must not be modified
	* @return A well-formed class file buffer (the result of
	* the transform), or null if no transform is performed
	* @throws IllegalClassFormatException If the input does
	* not represent a well-formed class file
	*/
	byte[] transform(ClassLoader loader, String className, 
		Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) 
        throws IllegalClassFormatException;
}
