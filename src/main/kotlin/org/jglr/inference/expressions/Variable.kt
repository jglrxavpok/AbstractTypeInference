package org.jglr.inference.expressions

open class Variable(val identifier: String) : Expression() {
    override val stringRepresentation: String = identifier
}