package org.jglr.inference.expressions

open class Function(val name: String, val argument: Expression, val expression: Expression) : Expression() {
    override val stringRepresentation: String = "$name := $argument -> $expression"

    operator fun invoke(arg: Expression): FunctionCall = FunctionCall(this, arg)
}