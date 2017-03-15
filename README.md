Abstract Type Inference
=======================

The goal of this library is to infer the type of a given expression made with simple blocks.

Elementary expressions
======================
Format: ```Expression type: KotlinConstructor(args)```

* Opaque expressions: ```OpaqueExpression(name)```
* Variable expressions: ```Variable(name)``` (similar to OpaqueExpression)
* Tuple expressions: ```Tuple(elements: Array<Expression>)```, must be used as an argument of a logically multi-argument function
* Function expressions: ```Function(name, argument: Expression, body: Expression)```
* Function calls: ```FunctionCall(function, argument)```
* Literals: ```Literal(userObject: Any, type: TypeDefinition)```, only expression to always have an explicit type

Elementary types
================
* Basic (or primitive) types: ```TypeDefinition()```
* Polymorphic types: ```PolyformicType()``` (**each** polyformic type has an UUID assigned on object creation)
* Function types: ```FunctionType(argument: TypeDefinition, returnType: TypeDefinition)```
* Tuple types: ```TupleType(elements: Array<TypeDefinition>)```

Rules for comparing elementary types
====================================
1. **All** types are <= than polyformic types.
2. Let a and b be two primitives types, ```a <= b iif a = b```
3. Let a and b be two function types, ```a <= b iif argument(a) <= argument(b) AND returnType(a) <= returnType(b)```
4. Let a and b be two tuple types, ```a <= b iif a and b have the same length AND for any valid index 'i', a(i) <= b(i)```
5. Anything else is considered impossible comparisons and therefore issues an IllegalArgumentException

Discriminating types when inferring expressions
===============================================
Let 'T' a non-empty list of types. The 'unified' type is ```min(T)``` in respect of comparison rules from *Rules for comparing elementary types*.