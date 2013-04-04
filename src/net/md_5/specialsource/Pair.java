/**
 * Copyright (c) 2012-2013, md_5. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 *
 * The name of the author may not be used to endorse or promote products derived
 * from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package net.md_5.specialsource;

import java.beans.ConstructorProperties;

import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

/**
 * A class representing a set of 2 objects as defined by the type parameters.
 *
 * @param <E> type of element
 */
@ToString
@EqualsAndHashCode
@RequiredArgsConstructor
public class Pair<E> {

    public final E first;
    public final E second;
    public String toString()
    {
      return "Pair(first=" + this.first + ", second=" + this.second + ")";
    }

    public boolean equals(Object o)
    {
      if (o == this)
        return true;
      if (!(o instanceof Pair))
        return false;
      Pair other = (Pair)o;
      if (!other.canEqual(this))
        return false;
      Object this$first = this.first;
      Object other$first = other.first;
      if (this$first == null ? other$first != null : !this$first.equals(other$first))
        return false;
      Object this$second = this.second;
      Object other$second = other.second;
      return this$second == null ? other$second == null : this$second.equals(other$second);
    }

    public boolean canEqual(Object other)
    {
      return other instanceof Pair;
    }

    public int hashCode()
    {
      int PRIME = 31;
      int result = 1;
      Object $first = this.first;
      result = result * 31 + ($first == null ? 0 : $first.hashCode());
      Object $second = this.second;
      result = result * 31 + ($second == null ? 0 : $second.hashCode());
      return result;
    }

    @ConstructorProperties({"first", "second"})
    public Pair(E first, E second)
    {
      this.first = first;
      this.second = second;
    }
  }