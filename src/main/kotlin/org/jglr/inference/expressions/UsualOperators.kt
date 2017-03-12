package org.jglr.inference.expressions

open class BinaryOperator(val left: Expression, val right: Expression, val operator: String) : Function("+", Tuple(left, right), OpaqueExpression("$left $operator $right")) {
    override val stringRepresentation: String = "$left $operator $right"
}

class Plus(left: Expression, right: Expression) : BinaryOperator(left, right, "+")
class Minus(left: Expression, right: Expression) : BinaryOperator(left, right, "-")
class Times(left: Expression, right: Expression) : BinaryOperator(left, right, "*")
class Div(left: Expression, right: Expression) : BinaryOperator(left, right, "/")