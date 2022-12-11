package sl.Compilation

import objects.types.*
import objects.*
import sl.*
import sl.Compilation.Selector.Selector

object Array{
    def generate(vari: Variable) = {
        val ArrayType(subType, nb) = vari.getType().asInstanceOf[ArrayType]

        val ctx = vari.context.push(vari.name)
        val getblock = Switch(VariableValue("index"), vari.tupleVari.zipWithIndex.map((v,i) => SwitchCase(IntValue(i), Return(LinkedVariableValue(v)))))
        val get = ConcreteFunction(ctx, f"get", List(Argument("index", IntType, None)), subType, vari.modifiers, getblock, false)
        get.generateArgument()(ctx)
        ctx.addFunction("get", get)

        val setblock = Switch(VariableValue("index"), vari.tupleVari.zipWithIndex.map((v,i) => SwitchCase(IntValue(i), VariableAssigment(List((Right(v), Selector.self)), "=", VariableValue("value")))))
        val set = ConcreteFunction(ctx, f"set", List(Argument("index", IntType, None), Argument("value", subType, None)), VoidType, vari.modifiers, setblock, false)
        set.generateArgument()(ctx)
        ctx.addFunction("set", set)
    }
}