package org.jglr.inference.types

import org.jglr.inference.expressions.TypeDefinition

class FunctionType(val argumentType: TypeDefinition, val returnType: TypeDefinition) : TypeDefinition() {
    override fun toString(): String = "$argumentType -> $returnType"

    override fun compare(other: TypeDefinition, firstCall: Boolean): Int {
        if(other is FunctionType) {
            if(argumentType <= other.argumentType && returnType <= other.returnType) {
                if(argumentType == other.argumentType && returnType == other.returnType) {
                    return 0
                }
                return -1
            } else {
                throw IllegalArgumentException("Cannot compare $this and $other")
            }
        } else {
            return super.compare(other, firstCall)
        }
    }
}