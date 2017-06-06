package org.jglr.inference.expressions

import org.jglr.inference.types.PolymorphicType
import org.jglr.inference.types.TypeDefinition

abstract class Expression {
    open var type: TypeDefinition = PolymorphicType()
    abstract val stringRepresentation: String

    override fun toString(): String = "($stringRepresentation : $type)"

    operator fun plus(right: Expression): Expression = Plus(this, right)
    operator fun minus(right: Expression): Expression = Minus(this, right)
    operator fun times(right: Expression): Expression = Times(this, right)
    operator fun div(right: Expression): Expression = Div(this, right)

    infix fun of(typeDefinition: TypeDefinition) = this.apply { type = typeDefinition }
}

class Literal(val representation: Any, typeDef: TypeDefinition) : Expression() {

    init {
        type = typeDef
    }

    override val stringRepresentation: String = representation.toString()
}