import junit.framework.Assert.assertEquals
import org.jglr.inference.ImpossibleUnificationExpression
import org.jglr.inference.TypeInferer
import org.jglr.inference.expressions.*
import org.jglr.inference.expressions.Function
import org.jglr.inference.types.PolyformicType
import org.jglr.inference.types.TypeDefinition
import org.junit.Test

class TestCustomType {

    @Test
    fun customType() {
        // let's create a custom 'list-like' type
        val Integers = object : TypeDefinition() { override fun toString(): String = "Integers" }
        val IntegerList = ListType(Integers)

        val inferer = TypeInferer()

        // Function that get the head of a list
        val variable = Variable("list") of ListType(PolyformicType())
        val funcExpr = object : Expression() {
            override val stringRepresentation: String
                get() = "hd ${variable.stringRepresentation}"

            override var type: TypeDefinition
                get() = (variable.type as ListType).component
                set(value) {}
        }
        println(funcExpr)
        val headFunction = Function("hd", variable, funcExpr)

        val mylist = Variable("mylist") of IntegerList
        val result = headFunction(mylist)

        inferer.infer(result)
        println(headFunction)
        println(">> "+inferer.unify(mylist.type, variable.type))
        println(variable)
        println(mylist)
        println(result)

        assertEquals(Integers, result.type)
    }

    @Test(expected = ImpossibleUnificationExpression::class)
    fun invalidUseOfCustomType() {
        // let's create a custom 'list-like' type
        val Integers = object : TypeDefinition() { override fun toString(): String = "Integers" }

        val inferer = TypeInferer()

        // Function that get the head of a list
        val variable = Variable("list") of ListType(PolyformicType())
        val funcExpr = object : Expression() {
            override val stringRepresentation: String
                get() = "hd ${variable.stringRepresentation}"

            override var type: TypeDefinition
                get() = (variable.type as ListType).component
                set(value) {}
        }
        println(funcExpr)
        val headFunction = Function("hd", variable, funcExpr)

        val someInteger = Variable("someInteger") of Integers

        // and use it with an integer (must fail)
        val result = headFunction(someInteger)

        inferer.infer(result)
    }
}

private class ListType(val component: TypeDefinition) : TypeDefinition() {
    override fun toString(): String = "$component list"

    override fun compare(other: TypeDefinition, firstCall: Boolean): Int {
        if(other is ListType) {
            if(component <= other.component) {
                return -1
            }
            if(!firstCall)
                throw IllegalArgumentException("Cannot compare lists: $this and $other")
        }
        return super.compare(other, firstCall)
    }
}