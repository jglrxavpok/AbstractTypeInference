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
                expr.left.type = value
                expr.right.type = value
                expr.type = value
            }
            is FunctionCall -> {
                infer(expr.function)
                val argumentType = unify(expr.argument.type, expr.function.argument.type)
                expr.argument.type = argumentType
                val result = expr.function.expression.type
                expr.type = result
            }
            is Function -> {
                infer(expr.argument)
                infer(expr.expression)
                expr.type = FunctionType(expr.argument.type, expr.expression.type)
            }
            is Tuple -> {
                expr.arguments.forEach { this::infer }
                val elementTypes = expr.arguments.map(Expression::type)
                expr.type = TupleType(elementTypes.toTypedArray())
            }
        }
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
