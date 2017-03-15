package org.jglr.inference.expressions

import com.sun.org.apache.xpath.internal.compiler.FunctionTable
import org.jglr.inference.types.FunctionType
import org.jglr.inference.types.TupleType

class TypeInferer {

    fun infer(expr: Expression) {
        when(expr) {
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
                infer(expr.expression)
                infer(expr.argument)
                updateType(expr, FunctionType(expr.argument.type, expr.expression.type))
            }
            is Tuple -> {
                expr.arguments.forEach { this::infer }
                val elementTypes = expr.arguments.map(Expression::type)
                updateType(expr, TupleType(elementTypes.toTypedArray()))
            }
        }
    }

    private fun updateType(expr: Expression, type: TypeDefinition) {
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
            is Function -> {
                if(type !is PolyformicType) {
                    if (type !is FunctionType)
                        throw ImpossibleUnificationExpression("Functions cannot have a type that is neither Polyformic nor a function type")

                    updateType(expr.argument, type.argumentType)
                    updateType(expr.expression, type.returnType)
                }
            }
        }
        expr.type = unify(expr.type, type)
    }

    fun unify(vararg types: TypeDefinition): TypeDefinition {
        if(types.isEmpty())
            throw IllegalArgumentException("Cannot unify array of 0 types")
        try {
            val min = types.min()
            return min!!
        } catch (e: IllegalArgumentException) {
            throw ImpossibleUnificationExpression(e.message)
        }
    }

    private fun fits(t: TypeDefinition, result: TypeDefinition): Boolean {
        return result is PolyformicType || result == t || (t is TupleType && isFitTuple(t, result)) || (t is FunctionType && isFitFunction(t, result))
    }

    private fun isFitFunction(t: FunctionType, result: TypeDefinition): Boolean {
        if(result !is FunctionType)
            throw ImpossibleUnificationExpression()
        return fits(t.argumentType, result.argumentType) && fits(t.returnType, result.returnType)
    }

    private fun isFitTuple(tupleType: TupleType, result: TypeDefinition): Boolean {
        if(result !is TupleType)
            throw ImpossibleUnificationExpression()
        return tupleType.elementTypes.filterIndexed { index, elem -> ! fits(elem, result.elementTypes[index]) }
                                    .isEmpty()
    }
}

class ImpossibleUnificationExpression(message: String? = "") : Exception(message) {

}
