package sl.Compilation

import objects.types.*
import objects.*
import sl.*
import sl.Compilation.Selector.Selector

object Array{
    def generate(vari: Variable) = {
        val ArrayType(subType, nb) = vari.getType().asInstanceOf[ArrayType]

        val ctx = vari.context.push(vari.name)
        val getblock = Switch(VariableValue("index"), vari.tupleVari.zipWithIndex.map((v,i) => SwitchCase(IntValue(i), Return(LinkedVariableValue(v)), BoolValue(true))))
        val get = ConcreteFunction(ctx, ctx.getPath()+".get", f"get", List(Argument("index", IntType, None)), subType, vari.modifiers, getblock, false)
        get.generateArgument()(ctx)
        ctx.addFunction("get", get)

        val setblock = Switch(VariableValue("index"), vari.tupleVari.zipWithIndex.map((v,i) => SwitchCase(IntValue(i), VariableAssigment(List((Right(v), Selector.self)), "=", VariableValue("value")), BoolValue(true))))
        val set = ConcreteFunction(ctx,ctx.getPath()+".set", f"set", List(Argument("index", IntType, None), Argument("value", subType, None)), VoidType, vari.modifiers, setblock, false)
        set.generateArgument()(ctx)
        ctx.addFunction("set", set)
    }

    def generate(vari: Variable, inner: List[(Variable, Expression)], size: Int) = {
        val ArrayType(subType, nb) = vari.getType().asInstanceOf[ArrayType]

        val indexes = TupleValue(Range(0, size).map(j => VariableValue(f"index_$j")).toList)
        val arguments = Range(0,size).map(j => Argument(f"index_$j", IntType, None)).toList

        val ctx = vari.context.push(vari.name)
        val getblock = Switch(indexes, inner.map{case (v, i) => SwitchCase(i, Return(LinkedVariableValue(v)), BoolValue(true))})
        val get = ConcreteFunction(ctx, ctx.getPath()+".get", f"get", arguments, subType, vari.modifiers, getblock, false)
        get.generateArgument()(ctx)
        ctx.addFunction("get", get)

        val setblock = Switch(indexes, inner.map{case (v, i) => SwitchCase(i, VariableAssigment(List((Right(v), Selector.self)), "=", VariableValue("value")), BoolValue(true))})
        val set = ConcreteFunction(ctx,ctx.getPath()+".set", f"set", arguments:::List(Argument("value", subType, None)), VoidType, vari.modifiers, setblock, false)
        set.generateArgument()(ctx)
        ctx.addFunction("set", set)
    }
}