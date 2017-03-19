package org.jglr.inference

import org.jglr.inference.expressions.*
import org.jglr.inference.expressions.Function
import org.jglr.inference.types.*
import org.jglr.inference.expressions.List as ListExpression

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
            val result = it.function.getAppliedReturnType(argumentType)
            updateType(it, result)
        }
        defineUpdatesOf { call: FunctionCall, type ->
            updateType(call.argument, call.function.getAppliedArgumentType(type))
        }

        defineProcessingOf<Function> {
            infer(it.expression)
            infer(it.argument)
            updateType(it, FunctionType(it.argument.type, it.expression.type))
        }
        defineUpdatesOf { expr: Function, type ->
            if(type !is PolymorphicType) {
                if (type !is FunctionType)
                    throw ImpossibleUnificationExpression("Functions cannot have a type that is neither Polymorphic nor a function type, found: $type")

                updateType(expr.argument, type.argumentType)
                updateType(expr.expression, type.returnType)
            }
        }

        defineProcessingOf<Tuple> {
            it.arguments.forEach { infer(it) }
            val elementTypes = it.arguments.map(Expression::type)
            updateType(it, TupleType(elementTypes.toTypedArray()))
        }
        defineUpdatesOf { expr: Tuple, type ->
            if(type !is PolymorphicType) {
                if(type !is TupleType)
                    throw ImpossibleUnificationExpression("Tuple cannot have a type that is neither Polymorphic nor a tuple type")

                expr.arguments.forEachIndexed { index, expression -> updateType(expression, type.elementTypes[index]) }
            }
        }

        defineProcessingOf<ListExpression> {
            it.elements.forEach { infer(it) }
            val types = it.elements.map(Expression::type).toTypedArray()
            val elementType = unify(*types)
            updateType(it, ListType(elementType))
        }

        defineUpdatesOf { list: ListExpression, type ->
            if(type !is PolymorphicType) {
                if(type !is ListType)
                    throw ImpossibleUnificationExpression("Lists cannot have a type that is neither Polymorphic nor a list type")

                list.elements.forEach { updateType(it, type.component) }
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
        addProcessor(object : TypeProcessor {
            override fun isHandled(type: Expression): Boolean = type is T
            override fun process(expr: Expression) = process(expr as T)
        })
    }

    inline fun <reified T : Expression> defineUpdatesOf(crossinline update: (T, TypeDefinition) -> Unit) {
        addUpdater(object : TypeUpdater {
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

interface TypeUpdater {
    fun isHandled(type: Expression): Boolean

    fun propagateUpdate(expr: Expression, type: TypeDefinition): Unit
}

interface TypeProcessor {
    fun isHandled(type: Expression): Boolean

    fun process(expr: Expression)
}

class ImpossibleUnificationExpression(message: String? = "") : Exception(message)
