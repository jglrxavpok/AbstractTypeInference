package org.jglr.inference.expressions

class TypeInferer {

    fun infer(expr: Expression) {
        when(expr) {
            is BinaryOperator -> {
                val value = unify(expr.left.type, expr.right.type)
                expr.left.type = value
                expr.right.type = value
                expr.type = value
            }
        }
    }

    fun unify(vararg types: TypeDefinition): TypeDefinition {
        var result: TypeDefinition = PolyformicType()
        for (t in types) {
            if(t !is PolyformicType) {
                val isFit = result is PolyformicType || result == t
                if(isFit)
                    result = t
                else
                    throw ImpossibleUnificationExpression("")
            } else {
                result = t
            }
        }
        return result
    }
}

class ImpossibleUnificationExpression(message: String = "") : Exception(message) {

}
