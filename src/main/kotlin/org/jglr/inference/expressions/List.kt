package org.jglr.inference.expressions

import kotlin.collections.List

class List(val elements: List<Expression>) : Expression() {
    override val stringRepresentation: String
        get() = if(elements.isEmpty()) "[]" else "["+elements.map(Expression::stringRepresentation).reduce {a, b -> "$a; $b"}+"]"
}