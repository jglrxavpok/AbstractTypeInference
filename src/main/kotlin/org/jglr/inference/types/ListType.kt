package org.jglr.inference.types

class ListType(val component: TypeDefinition) : TypeDefinition() {
    override fun toString(): String = "$component list"

    override fun equals(other: Any?): Boolean {
        if(other is ListType) {
            return component == other.component
        }
        return super.equals(other)
    }

    override fun compare(other: TypeDefinition, firstCall: Boolean): Int {
        if(other is ListType) {
            if(component <= other.component) {
                if(component == other.component)
                    return 0
                return -1
            }
            if(!firstCall)
                throw IllegalArgumentException("Cannot compare lists: $this and $other")
        }
        return super.compare(other, firstCall)
    }

    override fun hashCode(): Int {
        return component.hashCode()
    }
}