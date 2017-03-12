package org.jglr.inference.expressions

object ExpressionBuilder {

    fun functionCall(function: Function, input: Expression) = FunctionCall(function, input)
}