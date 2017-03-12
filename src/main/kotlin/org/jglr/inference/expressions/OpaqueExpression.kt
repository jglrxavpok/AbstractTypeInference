package org.jglr.inference.expressions

class OpaqueExpression(val representation: String) : Expression() {
    override val stringRepresentation: String = representation
}