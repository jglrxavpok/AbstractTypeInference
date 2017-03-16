package org.jglr.inference

import org.jglr.inference.expressions.*
import org.jglr.inference.expressions.Function
import org.jglr.inference.types.FunctionType
import org.jglr.inference.types.TupleType

class TypeInferer {

    private val processors = mutableListOf<TypeProcessor>()
    private val updaters = mutableListOf<TypeUpdater>()

    init {
        defineProcessingOf<BinaryOperator> {
            infer(it.left)
            infer(it.right)
            val value = unify(it.left.type, it.right.type)
            updateType(it, value)
        }
        defineUpdatesOf { expr: BinaryOperator, type ->
            updateType(expr.left, type)
            updateType(expr.right, type)
        }

        defineProcessingOf<FunctionCall> {
            infer(it.function)
            val argumentType = unify(it.argument.type, it.function.argument.type)
            updateType(it.argument, argumentType)
            val functionType = it.function.argument.type

            // This exploits the fact that some functions can have a return type linked to their argument type (ie getting the head of a list of <type> should yield an element of <type>)
            it.function.argument.type = argumentType
            val result = it.function.expression.type
            it.function.argument.type = functionType
            // End of exploit
            updateType(it, result)
        }
        // function calls have no special rules for updating their types

        defineProcessingOf<Function> {
            infer(it.expression)
            infer(it.argument)
            updateType(it, FunctionType(it.argument.type, it.expression.type))
        }
        defineUpdatesOf { expr: Function, type ->
            if(type !is PolyformicType) {
                if (type !is FunctionType)
                    throw ImpossibleUnificationExpression("Functions cannot have a type that is neither Polyformic nor a function type, found: $type")

                updateType(expr.argument, type.argumentType)
                updateType(expr.expression, type.returnType)
            }
        }

        defineProcessingOf<Tuple> {
            it.arguments.forEach { this::infer }
            val elementTypes = it.arguments.map(Expression::type)
            updateType(it, TupleType(elementTypes.toTypedArray()))
        }
        defineUpdatesOf { expr: Tuple, type ->
            if(type !is PolyformicType) {
                if(type !is TupleType)
                    throw ImpossibleUnificationExpression("Tuple cannot have a type that is neither Polyformic nor a tuple type")

                expr.arguments.forEachIndexed { index, expression -> updateType(expression, type.elementTypes[index]) }
            }
        }
    }

    fun addProcessor(processor: TypeProcessor) {
        processors.add(processor)
    }

    fun addUpdater(updater: TypeUpdater) {
        updaters.add(updater)
    }

    inline fun <reified T : Expression> defineProcessingOf(crossinline process: (T) -> Unit) {
        addProcessor(object : TypeProcessor() {
            override fun isHandled(type: Expression): Boolean = type is T
            override fun process(expr: Expression) = process(expr as T)
        })
    }

    inline fun <reified T : Expression> defineUpdatesOf(crossinline update: (T, TypeDefinition) -> Unit) {
        addUpdater(object : TypeUpdater() {
            override fun isHandled(type: Expression): Boolean = type is T
            override fun propagateUpdate(expr: Expression, type: TypeDefinition) = update(expr as T, type)
        })
    }

    fun infer(expr: Expression) {
        processors.find { it.isHandled(expr) }?.process(expr)
    }

    fun updateType(expr: Expression, type: TypeDefinition) {
        updaters.find { it.isHandled(expr) }?.propagateUpdate(expr, type)
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

}

abstract class TypeUpdater {
    abstract fun isHandled(type: Expression): Boolean

    abstract fun propagateUpdate(expr: Expression, type: TypeDefinition): Unit
}

abstract class TypeProcessor {
    abstract fun isHandled(type: Expression): Boolean

    abstract fun process(expr: Expression)
}

class ImpossibleUnificationExpression(message: String? = "") : Exception(message)
