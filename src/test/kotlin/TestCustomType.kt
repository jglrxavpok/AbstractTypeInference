import org.jglr.inference.ImpossibleUnificationExpression
import org.jglr.inference.TypeInferer
import org.jglr.inference.expressions.*
import org.jglr.inference.expressions.Function
import org.jglr.inference.types.PolymorphicType
import org.jglr.inference.types.TypeDefinition
import org.junit.Assert.assertEquals
import org.junit.Test

class TestCustomType {

    @Test
    fun customType() {
        // let's create a custom 'list-like' type
        val Integers = object : TypeDefinition() { override fun toString(): String = "Integers" }
        val IntegerList = ListType(Integers)

        val inferer = TypeInferer()

        // Function that get the head of a list
        val variable = Variable("list") of ListType(PolymorphicType())
        val funcExpr = OpaqueExpression("hd ${variable.stringRepresentation}")
        val headFunction = object : Function("hd", variable, funcExpr) {
            override fun getAppliedReturnType(argType: TypeDefinition): TypeDefinition {
                return (argType as ListType).component
            }

            override fun getAppliedArgumentType(returnType: TypeDefinition): TypeDefinition {
                return ListType(returnType)
            }
        }

        val mylist = Variable("mylist") of IntegerList
        val result = headFunction(mylist)

        inferer.infer(result)

        assertEquals(Integers, result.type)
    }

    @Test(expected = ImpossibleUnificationExpression::class)
    fun invalidUseOfCustomType() {
        // let's create a custom 'list-like' type
        val Integers = object : TypeDefinition() { override fun toString(): String = "Integers" }

        val inferer = TypeInferer()

        // Function that get the head of a list
        val variable = Variable("list") of ListType(PolymorphicType())
        val funcExpr = OpaqueExpression("hd ${variable.stringRepresentation}")
        val headFunction = object : Function("hd", variable, funcExpr) {
            override fun getAppliedReturnType(argType: TypeDefinition): TypeDefinition {
                return (argType as ListType).component
            }

            override fun getAppliedArgumentType(returnType: TypeDefinition): TypeDefinition {
                return ListType(returnType)
            }
        }


        val someInteger = Variable("someInteger") of Integers

        // and use it with an integer (must fail)
        val result = headFunction(someInteger)

        inferer.infer(result)
    }

    @Test
    fun inferCustomTypeWithOperations() {
        // let's create a custom 'list-like' type
        val Integers = object : TypeDefinition() { override fun toString(): String = "Integers" }
        val ListIntegers = ListType(Integers)
        val inferer = TypeInferer()

        // Function that get the head of a list
        val variable = Variable("list") of ListType(PolymorphicType())
        val funcExpr = OpaqueExpression("hd ${variable.stringRepresentation}")
        val headFunction = object : Function("hd", variable, funcExpr) {
            override fun getAppliedReturnType(argType: TypeDefinition): TypeDefinition {
                return (argType as ListType).component
            }

            override fun getAppliedArgumentType(returnType: TypeDefinition): TypeDefinition {
                return ListType(returnType)
            }
        }

        val mylist = Variable("mylist")
        val result = headFunction(mylist) + Literal(45, Integers)

        inferer.infer(result)
        println(result)
        println(funcExpr)
        println(variable)
        println(mylist)
        assertEquals(Integers, result.type)
        assertEquals(ListIntegers, mylist.type)
    }

}

private class ListType(val component: TypeDefinition) : TypeDefinition() {
    override fun toString(): String = "$component list"

    override fun equals(other: Any?): Boolean {
        if(other is ListType) {
            return component == other.component
        }
        return super.equals(other)
    }

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

    override fun hashCode(): Int {
        return component.hashCode()
    }
}