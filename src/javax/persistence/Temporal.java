/*
 * The contents of this file are subject to the terms 
 * of the Common Development and Distribution License 
 * (the "License").  You may not use this file except 
 * in compliance with the License.
 * 
 * You can obtain a copy of the license at 
 * glassfish/bootstrap/legal/CDDLv1.0.txt or 
 * https://glassfish.dev.java.net/public/CDDLv1.0.html. 
 * See the License for the specific language governing 
 * permissions and limitations under the License.
 * 
 * When distributing Covered Code, include this CDDL 
 * HEADER in each file and include the License file at 
 * glassfish/bootstrap/legal/CDDLv1.0.txt.  If applicable, 
 * add the following below this CDDL HEADER, with the 
 * fields enclosed by brackets "[]" replaced with your 
 * own identifying information: Portions Copyright [yyyy] 
 * [name of copyright owner]
 */
package javax.persistence;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * This annotation must be specified for persistent fields 
 * or properties of type {@link java.util.Date} and {@link 
 * java.util.Calendar}. It may only be specified for fields 
 * or properties of these types.
 * 
 * <p> The <code>Temporal</code> annotation may be used in 
 * conjunction with the {@link Basic} annotation.
 *
 * <pre>
 *     Example:
 * 
 *     &#064;Temporal(DATE)
 *     protected java.util.Date endDate;
 * </pre>
 *
 * @since Java Persistence 1.0
 */
@Target({METHOD, FIELD}) 
@Retention(RUNTIME)
public @interface Temporal {

    /** The type used in mapping java.util.Date or java.util.Calendar. */
    TemporalType value();
}
