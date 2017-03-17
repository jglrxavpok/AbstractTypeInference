import org.jglr.inference.expressions.*
import org.jglr.inference.expressions.Function
import org.jglr.inference.types.TypeDefinition
import org.junit.Test

class TestExpression {

    @Test
    fun buildSquare() {
        val Reals = object : TypeDefinition() {
            override fun toString(): String {
                return "Real"
            }
        }
        val x = Variable("x") of Reals
        val squareFunction = Function("square", x, x * x)
        println(squareFunction)
    }

    @Test
    fun buildIncr() {
        val Integers = object : TypeDefinition() {
            override fun toString(): String {
                return "Integer"
            }
        }
        val n = Variable("n") of Integers
        val incrFunction = Function("incr", n, n + Literal(1, Integers))
        println(incrFunction)
        println(incrFunction(Literal(45, Integers)))
    }
}