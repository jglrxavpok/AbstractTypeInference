import org.jglr.inference.ImpossibleUnificationExpression
import org.jglr.inference.TypeInferer
import org.jglr.inference.expressions.*
import org.jglr.inference.expressions.Function
import org.jglr.inference.types.FunctionType
import org.jglr.inference.types.TupleType
import org.jglr.inference.types.TypeDefinition
import org.junit.Test
import org.junit.Assert.*

class TestInference {

    @Test
    fun inferBinaryOperator() {
        val Integers = object : TypeDefinition() {
            override fun toString(): String = "Integer"
        }
        val a = Literal(1, Integers)
        val b = Literal(1, Integers)
        val inferer = TypeInferer()
        val result = a+b
        inferer.infer(result)
        assertEquals(Integers, result.type)
    }

    @Test(expected = ImpossibleUnificationExpression::class)
    fun impossibleUnification() {
        val Integers = object : TypeDefinition() {
            override fun toString(): String = "Integer"
        }
        val Reals = object : TypeDefinition() {
            override fun toString(): String = "Real"
        }
        val a = Literal(1, Integers)
        val b = Literal(1f, Reals)
        val inferer = TypeInferer()
        val result = a+b
        inferer.infer(result)
        assertEquals(result.type, Integers)
    }

    @Test
    fun inferPolymorphicWithFixedType() {
        val inferer = TypeInferer()
        val Integers = object : TypeDefinition() {
            override fun toString(): String = "Integer"
        }
        val a = Literal(0, Integers)
        val b = object : Expression() {
            override val stringRepresentation: String = "b"
        }
        val result = (a / b) as BinaryOperator
        inferer.infer(result)
        assertEquals(result.left.type, result.right.type)
        assertEquals(result.left.type, result.type)
        assertEquals(result.type, Integers)
    }

    @Test
    fun inferPolymorphic() {
        val inferer = TypeInferer()
        val a = object : Expression() {
            override val stringRepresentation: String = "a"
        }
        val b = object : Expression() {
            override val stringRepresentation: String = "b"
        }
        val result = (a / b) as BinaryOperator
        inferer.infer(result)
        assertTrue(result.left.type == result.right.type)
        assertTrue(result.left.type == result.type)
    }

    @Test
    fun inferFunctionType() {
        val inferer = TypeInferer()
        val Reals = object : TypeDefinition() {
            override fun toString(): String = "Real"
        }
        val x = Variable("x") of Reals
        val func = Function("double", x, x+x)
        inferer.infer(func)
        assertTrue(func.type is FunctionType && (func.type as FunctionType).argumentType == Reals && (func.type as FunctionType).returnType == Reals)

        val applied = func(Literal(0, Reals))
        inferer.infer(applied)
        println(func)
        println(applied)
        assertTrue(applied.type == Reals)
    }

    @Test
    fun inferMultipleArgFunctionType() {
        val inferer = TypeInferer()
        val Reals = object : TypeDefinition() {
            override fun toString(): String = "Real"
        }
        val Integers = object : TypeDefinition() {
            override fun toString(): String = "Integer"
        }
        val x = Variable("x") of Reals
        val y = Variable("y") of Integers
        val realOfInt = Function("real_of_int", Variable("x") of Integers, OpaqueExpression("real(x)") of Reals)
        inferer.infer(realOfInt)
        val func = Function("somefunc", Tuple(x, y), realOfInt(y)+x)
        inferer.infer(func)

        assertTrue(realOfInt.type is FunctionType && realOfInt.argument.type == Integers && realOfInt.expression.type == Reals)
        assertTrue(func.type is FunctionType && (func.type as FunctionType).argumentType is TupleType
                && ((func.type as FunctionType).argumentType as TupleType).elementTypes[0] == Reals
                && ((func.type as FunctionType).argumentType as TupleType).elementTypes[1] == Integers)
    }

    @Test
    fun inferComplexExpressionWithAlmostNoExplicitTyping() {
        val inferer = TypeInferer()
        val Reals = object : TypeDefinition() {
            override fun toString(): String = "Real"
        }
        val Integers = object : TypeDefinition() {
            override fun toString(): String = "Integer"
        }
        val someFuncWithInt = Function("funcint", Variable("n") of Integers, OpaqueExpression("n!") of Reals)
        val a = Variable("a")
        val b = Variable("b")
        val funcApplied = someFuncWithInt(b)
        val result = a+a - funcApplied
        val f = Function("f", Tuple(a, b), result)

        val someObjectA = object : Expression() {override val stringRepresentation: String = "A"}
        val someObjectB = object : Expression() {override val stringRepresentation: String = "B"}
        val complexExpression = f(Tuple(someObjectA, someObjectB))
        inferer.infer(complexExpression)

        println(f)

        println(complexExpression)

        assertTrue(someObjectA.type == Reals)
        assertTrue(someObjectB.type == Integers)
    }


}