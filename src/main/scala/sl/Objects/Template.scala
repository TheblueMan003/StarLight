package objects

import types.*
import sl.{Instruction, InstructionList, Utils}
import sl.StructDecl
import sl.ClassDecl
import sl.EnumDecl
import sl.PredicateDecl
import sl.FunctionDecl
import sl.TemplateDecl
import sl.BlocktagDecl
import sl.TypeDef
import sl.ForGenerate
import sl.ForEach
import sl.VariableDecl
import sl.VariableAssigment
import sl.ArrayAssigment
import sl.CMD
import sl.FunctionCall
import sl.TemplateUse
import sl.LinkedFunctionCall
import sl.ElseIf
import sl.If
import sl.Return
import sl.Import
import sl.Switch
import sl.WhileLoop
import sl.DoWhileLoop
import sl.InstructionBlock
import sl.Execute
import sl.With

class Template(context: Context, name: String, _modifier: Modifier, val block: Instruction, val parent: Template) extends CObject(context, name, _modifier) with Typed(IdentifierType(context.getPath()+"."+name)){
    def getBlock(): Instruction = {
        if (parent != null){
            InstructionList(List(parent.getBlock(), Utils.fix(block)(context, Set())))
        }
        else{
            Utils.fix(block)(context, Set())
        }
    }
    def getContext():Context={
        val sub = context.push(name)
        if (parent != null){
            sub.inherit(parent.getContext())
        }
        sub
    }
    
}