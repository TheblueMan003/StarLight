package fos

import objects.{Context, ConcreteFunction, LazyFunction, Struct, Modifier, Variable}
import objects.types.VoidType

object ContextBuilder{
    def build(name: String, inst: Instruction):Context = {
        val context = Context.getNew(name)
        buildRec(inst)(context)
        Compiler.compile(inst, true)(context)
        context
    }

    private def buildRec(inst: Instruction)(context: Context):Unit = {
        inst match{
            case StructDecl(name, block, modifier) => {
                context.addStruct(new Struct(context, name, modifier, block))
            }
            case Package(name, block) => {
                buildRec(block)(context.push(name))
            }
            case InstructionList(block) => {
                block.foreach(p => buildRec(p)(context))
            }
            case InstructionBlock(block) => {
                block.foreach(p => buildRec(p)(context))
            }
            case _ => {
            }
        }
    }
}