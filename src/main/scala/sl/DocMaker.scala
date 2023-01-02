package sl

import objects.Context
import objects.Modifier

object DocMaker{
    /**
     * Traverse the instruction tree and generate the html documentation from modifiers and comments
     */
    def make2(instr: Instruction)(implicit packag: String = ""):String = {
        instr match
            case i: InstructionBlock => i.list.map(make2).mkString("")
            case If(cond, ifBlock, elze) => make2(ifBlock) + elze.flatMap(g => make2(g.ifBlock)).mkString("")
            case Package(name, block) => make2(block)(name)
            case FunctionDecl(name, block, typ, args, modifier) => 
                ContentMaker.h2(modifier.schema() +" "+typ.toString()+ " "+packag+"."+name+"("+args.map(a => a.typ.toString()+" "+a.name).mkString(", ")+")")+
                args.map(a => "- "+a.typ.toString()+" " + a.name).mkString("\n")+(if (args.length>0)then "\n\n" else "")+
                ContentMaker.p(modifier.doc)+"\n\n"

            case StructDecl(name, block, modifier, parent) => 
                    ContentMaker.h1(modifier.schema()+" struct "+packag+"."+name)+
                    ContentMaker.p(modifier.doc)+"\n\n"
                + make2(block)+"\n\n"
            case ClassDecl(name, block, modifier, parent, entity) => 
                    ContentMaker.h1(modifier.schema()+" class "+packag+"."+name)+
                    ContentMaker.p(modifier.doc)+"\n\n"
                 + make2(block)+"\n\n"
            case TemplateDecl(name, block, modifier, parent) => 
                    ContentMaker.h1(modifier.schema()+" template "+packag+"."+name)+
                    ContentMaker.p(modifier.doc)+"\n\n"
                 + make2(block)+"\n\n"
            case ForEach(key, provider, instr) => make2(instr)
            case ForGenerate(key, provider, instr) => make2(instr)
            case WhileLoop(cond, block) => make2(block)
            case Execute(typ, exprs, block) => make2(block)
            case InstructionList(list) => list.map(make2).reduceOption(_ + _).getOrElse("")
            case JSONFile(name, json, modifier) => 
                ContentMaker.h2(modifier.schema()+" jsonfile "+packag+"."+name)+
                ContentMaker.p(modifier.doc)+"\n\n"
            case PredicateDecl(name, args, block, modifier) => 
                ContentMaker.h2(modifier.schema()+" predicate "+packag+"."+name)+
                ContentMaker.p(modifier.doc)+"\n\n"
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