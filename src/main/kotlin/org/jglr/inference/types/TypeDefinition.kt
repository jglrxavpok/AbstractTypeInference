package org.jglr.inference.types

import java.util.*

class PolymorphicType : TypeDefinition() {
    val id: UUID = UUID.randomUUID()

    override fun toString(): String = "Poly($id)"

    override fun compare(other: TypeDefinition, firstCall: Boolean): Int {
        if(other == this)
            return 0
        return 1
    }
}

open class TypeDefinition : Comparable<TypeDefinition> {
    override fun equals(other: Any?): Boolean = other === this

    open fun compare(other: TypeDefinition, firstCall: Boolean): Int {
        if(equals(other))
            return -1
        else if(firstCall)
            return -other.compare(this, false)
        else
            throw IllegalArgumentException("Cannot compare $this and $other")
    }

    override operator fun compareTo(other: TypeDefinition): Int = compare(other, true)

    override fun hashCode(): Int {
        return toString().hashCode()
    }
}
