package org.jglr.inference.expressions

import org.jglr.inference.types.TypeDefinition

open class Function(val name: String, val argument: Expression, val expression: Expression) : Expression() {
    override val stringRepresentation: String
        get() = "$name : (${argument.stringRepresentation} -> ${expression.stringRepresentation})"

    operator fun invoke(arg: Expression): FunctionCall = FunctionCall(this, arg)

    open fun getAppliedReturnType(argType: TypeDefinition): TypeDefinition = expression.type

    open fun getAppliedArgumentType(returnType: TypeDefinition): TypeDefinition = argument.type
}