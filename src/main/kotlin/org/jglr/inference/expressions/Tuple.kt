package org.jglr.inference.expressions

class Tuple(vararg val arguments: Expression) : Expression() {
    override val stringRepresentation: String
        get() = if (arguments.isEmpty()) "()" else "("+arguments.map(Expression::stringRepresentation).reduce {a, b -> "$a, $b"}+")"
}