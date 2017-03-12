package org.jglr.inference.expressions

class Tuple(vararg val arguments: Expression) : Expression() {
    override val stringRepresentation: String = "("+arguments.map(Expression::toString).reduce {a, b -> "$a, $b"}+")"
}