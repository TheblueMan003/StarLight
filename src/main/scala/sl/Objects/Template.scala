package objects

import types.*
import sl.{Instruction, InstructionList, Utils}
import sl.Expression

class Template(context: Context, name: String, _modifier: Modifier, val block: Instruction, val parentName: Identifier, val generics: List[String], val parentGenerics: List[Expression]) extends CObject(context, name, _modifier){
    lazy val parent = if (parentName == null) null else context.getTemplate(parentName)
    def getSelfBlock(values: List[Expression]) = {
        if (values.size != generics.size){
            throw new Exception("Wrong number of arguments")
        }
        generics.zip(values).foldLeft(Utils.fix(block.unBlockify())(context, Set()))((b, v) => Utils.subst(b, v._1, v._2))
    }
    def getBlock(values: List[Expression]): Instruction = {
        if (parent != null){
            var p = parentGenerics.map(g => 
                generics.zip(values).foldLeft(Utils.fix(g)(context, Set()))((b, v) => Utils.subst(b, v._1, v._2))
            )
            InstructionList(List(parent.getBlock(p), getSelfBlock(values)))
        }
        else{
            getSelfBlock(values)
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