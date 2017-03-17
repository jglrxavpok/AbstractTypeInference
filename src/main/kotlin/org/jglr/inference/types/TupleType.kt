package org.jglr.inference.types

import org.jglr.inference.expressions.Expression

class TupleType(val elementTypes: Array<TypeDefinition>) : TypeDefinition() {

    override fun compare(other: TypeDefinition, firstCall: Boolean): Int {
        if(other is TupleType) {
            if(other.elementTypes.size != elementTypes.size)
                throw IllegalArgumentException("Cannot compare tuples with different sizes: $this and $other")

            val compatibleTypes = elementTypes.filterIndexed { index, elem -> elem > other.elementTypes[index] }.isEmpty()
            if(compatibleTypes)
                return -1
            else if(firstCall)
                return other.compare(this, false)
            else
                throw IllegalArgumentException("Cannot compare tuples: $this and $other")
        } else {
            return super.compare(other, firstCall)
        }
    }

    override fun toString(): String = "("+elementTypes.map (TypeDefinition::toString).reduce { a, b -> "$a, $b"}+")"
}