package org.jglr.inference.expressions

class FunctionCall(val function: Function, val argument: Expression) : Expression() {
    override val stringRepresentation: String
        get() = "${function.name}($argument)"
}