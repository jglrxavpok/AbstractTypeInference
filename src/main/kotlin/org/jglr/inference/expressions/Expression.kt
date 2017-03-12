package org.jglr.inference.expressions

import java.util.*

abstract class Expression {
    open var type: TypeDefinition = PolyformicType()
    abstract val stringRepresentation: String

    override fun toString(): String = "($stringRepresentation : $type)"

    operator fun plus(right: Expression): Expression = Plus(this, right)
    operator fun minus(right: Expression): Expression = Minus(this, right)
    operator fun times(right: Expression): Expression = Times(this, right)
    operator fun div(right: Expression): Expression = Div(this, right)

    infix fun of(typeDefinition: TypeDefinition) = this.apply { type = typeDefinition }
}

class PolyformicType : TypeDefinition() {
    val id = UUID.randomUUID()

    override fun toString(): String = "Poly($id)"
}

class Literal(val representation: Any, typeDef: TypeDefinition) : Expression() {
    init {
        type = typeDef
    }

    override val stringRepresentation: String = representation.toString()
}

open class TypeDefinition {

}
