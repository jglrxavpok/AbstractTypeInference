package org.jglr.inference.expressions

class Variable(val identifier: String) : Expression() {
    override val stringRepresentation: String = identifier
}