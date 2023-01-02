package sl

import objects.Context
import objects.Modifier

object DocMaker{
    /**
     * Traverse the instruction tree and generate the html documentation from modifiers and comments
     */
    def make2(instr: Instruction):String = {
        instr match
            case i: InstructionBlock => i.list.map(make2).reduceOption(_ +"\n"+ _).getOrElse("")
            case If(cond, ifBlock, elze) => make2(ifBlock) + elze.flatMap(g => make2(g.ifBlock))
            case Package(name, block) => make2(block)
            case FunctionDecl(name, block, typ, args, modifier) => 
                ContentMaker.h2(name)+
                args.map(a => "- "+a.typ.toString()+" " + a.name).reduceOption(_+"\n"+_).getOrElse("")+"\n"+
                ContentMaker.p(modifier.doc)

            case StructDecl(name, block, modifier, parent) => 
                    ContentMaker.h1(name)+
                    ContentMaker.p(modifier.doc)
                + make2(block)
            case ClassDecl(name, block, modifier, parent, entity) => 
                    ContentMaker.h1(name)+
                    ContentMaker.p(modifier.doc)
                 + make2(block)
            case TemplateDecl(name, block, modifier, parent) => 
                    ContentMaker.h1(name)+
                    ContentMaker.p(modifier.doc)
                 + make2(block)
            case ForEach(key, provider, instr) => make2(instr)
            case ForGenerate(key, provider, instr) => make2(instr)
            case WhileLoop(cond, block) => make2(block)
            case Execute(typ, exprs, block) => make2(block)
            case InstructionList(list) => list.map(make2).reduceOption(_ +"\n"+ _).getOrElse("")
            case JSONFile(name, json, modifier) => 
                ContentMaker.h2(name)+
                ContentMaker.p(modifier.doc)
            case PredicateDecl(name, args, block, modifier) => 
                ContentMaker.h2(name)+
                ContentMaker.p(modifier.doc)
            case With(expr, isat, cond, block) => make2(block)
            case _ => ""
    }
}

object ContentMaker{
    def h1(text: String)={
        f"""# ${text.trim()}\n"""
    }

    def h2(text: String)={
        f"""## ${text.trim()}\n"""
    }

    def p(text: String)={
        text.trim()
    }
}