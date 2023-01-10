package sl

import objects.Context
import objects.Modifier
import objects.Protection.Private

object DocMaker{
    def makeIndex(files: List[String])={
        "# List of Libraries\n\n"+
        files.map(f => f"[$f](libraries/$f/index.html)").mkString("\n\n")
    }
    /**
     * Traverse the instruction tree and generate the html documentation from modifiers and comments
     */
    def make2(instr: Instruction)(implicit packag: String = ""):String = {
        instr match
            case i: InstructionBlock => i.list.map(make2).mkString("")
            case If(cond, ifBlock, elze) => make2(ifBlock) + elze.flatMap(g => make2(g.ifBlock)).mkString("")
            case Package(name, block) => make2(block)(name)

            case FunctionDecl(name, block, typ, args, List(), modifier) if modifier.protection != Private => 
                ContentMaker.h2("`"+modifier.schema() +" "+typ.toString()+ " "+name+"("+args.map(a => a.typ.toString()+" "+a.name).mkString(", ")+")`")+
                ContentMaker.p(modifier.doc)+"\n\n"

            case FunctionDecl(name, block, typ, args, typeargs, modifier) if modifier.protection != Private => 
                ContentMaker.h2("`"+modifier.schema() +" "+typ.toString()+ " "+name+typeargs.mkString("<", ",", ">")+"("+args.map(a => a.typ.toString()+" "+a.name).mkString(", ")+")`")+
                ContentMaker.p(modifier.doc)+"\n\n"

            case StructDecl(name, List(), block, modifier, parent) if modifier.protection != Private => 
                    ContentMaker.h1(modifier.schema()+" struct "+name)+
                    ContentMaker.p(modifier.doc)+"\n\n"
                + make2(block)+"\n\n"
            case ClassDecl(name, List(), block, modifier, parent, entity) if modifier.protection != Private => 
                    ContentMaker.h1(modifier.schema()+" class "+name)+
                    ContentMaker.p(modifier.doc)+"\n\n"
                 + make2(block)+"\n\n"
            
            case StructDecl(name, generics, block, modifier, parent) if modifier.protection != Private => 
                    ContentMaker.h1(modifier.schema()+" struct "+name+ generics.mkString("<", ",", ">"))+
                    ContentMaker.p(modifier.doc)+"\n\n"
                + make2(block)+"\n\n"
            case ClassDecl(name, generics, block, modifier, parent, entity) if modifier.protection != Private => 
                    ContentMaker.h1(modifier.schema()+" class "+name + generics.mkString("<", ",", ">"))+
                    ContentMaker.p(modifier.doc)+"\n\n"
                 + make2(block)+"\n\n"
            case TemplateDecl(name, block, modifier, parent) if modifier.protection != Private => 
                    ContentMaker.h1(modifier.schema()+" template "+name)+
                    ContentMaker.p(modifier.doc)+"\n\n"
                 + make2(block)+"\n\n"
            case ForEach(key, provider, instr) => make2(instr)
            case ForGenerate(key, provider, instr) => make2(instr)
            case WhileLoop(cond, block) => make2(block)
            case Execute(typ, exprs, block) => make2(block)
            case InstructionList(list) => list.map(make2).reduceOption(_ + _).getOrElse("")
            case JSONFile(name, json, modifier) if modifier.protection != Private => 
                ContentMaker.h2(modifier.schema()+" jsonfile "+name)+
                ContentMaker.p(modifier.doc)+"\n\n"
            case PredicateDecl(name, args, block, modifier) if modifier.protection != Private => 
                ContentMaker.h2(modifier.schema()+" predicate "+name)+
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