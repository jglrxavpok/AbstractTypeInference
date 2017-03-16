package org.jglr.inference.expressions

class Tuple(vararg val arguments: Expression) : Expression() {
    override val stringRepresentation: String
        get() = "("+arguments.map(Expression::stringRepresentation).reduce {a, b -> "$a, $b"}+")"
}