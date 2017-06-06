package org.jglr.inference.expressions

open class OpaqueExpression(val representation: String) : Expression() {
    override val stringRepresentation: String = representation
}