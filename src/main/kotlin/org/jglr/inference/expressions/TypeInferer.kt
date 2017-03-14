package org.jglr.inference.expressions

import org.jglr.inference.types.FunctionType
import org.jglr.inference.types.TupleType

class TypeInferer {

    fun infer(expr: Expression) {
        when(expr) {
            // TODO: handle (a+a) type set to Real and a not changing type to a Real
            is BinaryOperator -> {
                infer(expr.left)
                infer(expr.right)
                val value = unify(expr.left.type, expr.right.type)
                updateType(expr, value)
            }
            is FunctionCall -> {
                infer(expr.function)
                val argumentType = unify(expr.argument.type, expr.function.argument.type)
                updateType(expr.argument, argumentType)
                val result = expr.function.expression.type
                updateType(expr, result)
            }
            is Function -> {
                infer(expr.argument)
                infer(expr.expression)
                updateType(expr, FunctionType(expr.argument.type, expr.expression.type))
            }
            is Tuple -> {
                expr.arguments.forEach { this::infer }
                val elementTypes = expr.arguments.map(Expression::type)
                updateType(expr, TupleType(elementTypes.toTypedArray()))
            }
        }
    }

    private fun  updateType(expr: Expression, type: TypeDefinition) {
        when(expr) {
            is BinaryOperator -> {
                updateType(expr.left, type)
                updateType(expr.right, type)
            }
            is Tuple -> {
                if(type !is PolyformicType) {
                    if(type !is TupleType)
                        throw ImpossibleUnificationExpression("Tuple cannot have a type that is neither Polyformic nor a tuple type")

                    expr.arguments.forEachIndexed { index, expression -> updateType(expression, type.elementTypes[index]) }
                }
            }
        }
        expr.type = type
    }

    fun unify(vararg types: TypeDefinition): TypeDefinition {
        var result: TypeDefinition = PolyformicType()
        for(t in types) {
            if(t !is PolyformicType) {
                if(fits(t, result))
                    result = t
                else
                    throw ImpossibleUnificationExpression("Cannot unify types $result and $t")
            }
        }
        return result
    }

    private fun  fits(t: TypeDefinition, result: TypeDefinition): Boolean {
        return result is PolyformicType || result == t || (t is TupleType && isFitTuple(t, result))
    }

    private fun isFitTuple(tupleType: TupleType, result: TypeDefinition): Boolean {
        if(result !is TupleType)
            throw ImpossibleUnificationExpression()
        return tupleType.elementTypes.filterIndexed { index, elem -> ! fits(elem, result.elementTypes[index]) }
                                    .isEmpty()
    }
}

class ImpossibleUnificationExpression(message: String = "") : Exception(message) {

}
