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

import lombok.Data;

/**
 * A class which can be used to represent a field, method, or anything else
 * which has an owner, a name and a descriptor.
 */
@Data
public class Ownable {

    final NodeType type;
    final String owner;
    final String name;
    final String descriptor;
    
    @ConstructorProperties({"type", "owner", "name", "descriptor"})
    public Ownable(NodeType type, String owner, String name, String descriptor)
    {
      this.type = type;
      this.owner = owner;
      this.name = name;
      this.descriptor = descriptor;
    }

    public NodeType getType()
    {
      return this.type;
    }

    public String getOwner()
    {
      return this.owner;
    }

    public String getName()
    {
      return this.name;
    }

    public String getDescriptor()
    {
      return this.descriptor;
    }

    public boolean equals(Object o)
    {
      if (o == this)
        return true;
      if (!(o instanceof Ownable))
        return false;
      Ownable other = (Ownable)o;
      if (!other.canEqual(this))
        return false;
      Object this$type = getType();
      Object other$type = other.getType();
      if (this$type == null ? other$type != null : !this$type.equals(other$type))
        return false;
      Object this$owner = getOwner();
      Object other$owner = other.getOwner();
      if (this$owner == null ? other$owner != null : !this$owner.equals(other$owner))
        return false;
      Object this$name = getName();
      Object other$name = other.getName();
      if (this$name == null ? other$name != null : !this$name.equals(other$name))
        return false;
      Object this$descriptor = getDescriptor();
      Object other$descriptor = other.getDescriptor();
      return this$descriptor == null ? other$descriptor == null : this$descriptor.equals(other$descriptor);
    }

    public boolean canEqual(Object other)
    {
      return other instanceof Ownable;
    }

    public int hashCode()
    {
      int PRIME = 31;
      int result = 1;
      Object $type = getType();
      result = result * 31 + ($type == null ? 0 : $type.hashCode());
      Object $owner = getOwner();
      result = result * 31 + ($owner == null ? 0 : $owner.hashCode());
      Object $name = getName();
      result = result * 31 + ($name == null ? 0 : $name.hashCode());
      Object $descriptor = getDescriptor();
      result = result * 31 + ($descriptor == null ? 0 : $descriptor.hashCode());
      return result;
    }

    public String toString()
    {
      return "Ownable(type=" + getType() + ", owner=" + getOwner() + ", name=" + getName() + ", descriptor=" + getDescriptor() + ")";
    }
}
