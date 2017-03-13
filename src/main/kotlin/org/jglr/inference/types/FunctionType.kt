package org.jglr.inference.types

import org.jglr.inference.expressions.TypeDefinition

class FunctionType(val argumentType: TypeDefinition, val returnType: TypeDefinition) : TypeDefinition() {
    override fun toString(): String = "$argumentType -> $returnType"
}