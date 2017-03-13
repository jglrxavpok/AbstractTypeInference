package org.jglr.inference.types

import org.jglr.inference.expressions.Expression
import org.jglr.inference.expressions.TypeDefinition

class TupleType(val elementTypes: Array<TypeDefinition>) : TypeDefinition() {

    override fun toString(): String = "("+elementTypes.map (TypeDefinition::toString).reduce { a, b -> "$a, $b"}+")"
}