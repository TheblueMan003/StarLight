package sl

import objects.Context
import objects.Modifier
import objects.Protection.Private

object DocMaker{
    def listToMarkdownTree(files: List[(String, String)]): String = {
        def formatTree(files: List[(String, String)], indent: String = ""): String = {
            val (directories, remainingFiles) = files.partition(_._1.contains('/'))

            val directoryTree = directories.map { dir =>
                    val (dirName, subFiles) = dir._1.splitAt(dir._1.indexOf('/') + 1)
                    (dirName, subFiles, dir._2)
                }
                .groupBy(_._1).map((key, values) => {
                    (key, s"$indent- **${key.replace("/", "")}**\n" + formatTree(values.map(f => (f._2, f._3)), indent + "\t"))
                })
                .toList
                .sortBy(_._1)
                .map(_._2)
                .mkString("\n")

            val fileTree = remainingFiles.map(file => s"$indent- [${file._1}](libraries/${file._2}.md)\n").mkString

            directoryTree + fileTree
        }

        formatTree(files)
    }
    def makeIndex(files: List[String])={
        Utils.getFile("README.md")+
        "\n# List of Libraries\n\n"+listToMarkdownTree(files.zip(files))
        //files.map(f => f"- [${f.replace("/", ".")}](libraries/$f.md)").mkString("\n")
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
                ContentMaker.h2(modifier.schema() + " function " + name)+
                ContentMaker.p("### Arguments:\n"+args.map(a => f"- ${a.name} (`${a.typ}`)").mkString("\n")+f"\n### Return:\n- $typ")+"\n\n\n"+
                ContentMaker.p(modifier.doc)+"\n\n"

            case FunctionDecl(name, block, typ, args, typeargs, modifier) if modifier.protection != Private => 
                ContentMaker.h2("`"+modifier.schema() + " function" + name)+
                ContentMaker.p("### Type Arguments:\n"+typeargs.map(a => f"- `${a}`").mkString("\n")+"\n### Arguments:\n"+args.map(a => f"- ${a.name} (`${a.typ}`)").mkString("\n")+f"\n\n### Return:\n- `$typ`")+"\n\n\n"+
                ContentMaker.p(modifier.doc)+"\n\n"

            case StructDecl(name, List(), block, modifier, parent) if modifier.protection != Private => 
                    ContentMaker.h1(modifier.schema()+" struct "+name)+
                    ContentMaker.p(modifier.doc)+"\n\n"
                + make2(block)+"\n\n"
            case ClassDecl(name, List(), block, modifier, parent, parentGenerics, interfaces, entity) if modifier.protection != Private => 
                    ContentMaker.h1(modifier.schema()+" class "+name)+
                    ContentMaker.p(modifier.doc)+"\n\n"
                 + make2(block)+"\n\n"
            
            case StructDecl(name, generics, block, modifier, parent) if modifier.protection != Private => 
                    ContentMaker.h1(modifier.schema()+" struct "+name+ generics.mkString("<", ",", ">"))+
                    ContentMaker.p(modifier.doc)+"\n\n"
                + make2(block)+"\n\n"
            case ClassDecl(name, generics, block, modifier, parent, parentGenerics, interfaces, entity) if modifier.protection != Private => 
                    ContentMaker.h1(modifier.schema()+" class "+name + generics.mkString("<", ",", ">"))+
                    ContentMaker.p(modifier.doc)+"\n\n"
                 + make2(block)+"\n\n"
            case TemplateDecl(name, block, modifier, parent, generics, parentGenerics) if modifier.protection != Private => 
                    ContentMaker.h1(modifier.schema()+" template "+name + generics.mkString("<", ",", ">"))+
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
            case With(expr, isat, cond, block, elze) => make2(block) + "\n\n" + make2(elze)
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