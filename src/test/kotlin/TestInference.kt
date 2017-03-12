import org.jglr.inference.expressions.*
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
        assertEquals(result.type, Integers)
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
        assertTrue(result.left.type == result.right.type && result.left.type == result.type)
    }
}