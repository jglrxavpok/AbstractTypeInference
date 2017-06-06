package org.jglr.inference.types

class FunctionType(val argumentType: TypeDefinition, val returnType: TypeDefinition) : TypeDefinition() {
    override fun toString(): String = "$argumentType -> $returnType"

    override fun equals(other: Any?): Boolean {
        if(other is FunctionType) {
            return other.argumentType == argumentType && other.returnType == returnType
        }
        return super.equals(other)
    }

    override fun compare(other: TypeDefinition, firstCall: Boolean): Int {
        if(other is FunctionType) {
            if (argumentType <= other.argumentType && returnType <= other.returnType) {
                if (argumentType == other.argumentType && returnType == other.returnType) {
                    return 0
                }
                return -1
            } else if (!firstCall) {
                throw IllegalArgumentException("Cannot compare $this and $other")
            }
        }
        return super.compare(other, firstCall)
    }

    override fun hashCode(): Int {
        var result = argumentType.hashCode()
        result = 31 * result + returnType.hashCode()
        return result
    }
}