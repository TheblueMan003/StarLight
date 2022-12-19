package sl

import objects.Context
import objects.Modifier

object DocMaker{
    def make(context: Context, prefix: String): String={
        val template = Utils.getResources("docs/template.html")
        val fct = context.getAllFunction().filter(_.fullName.contains(prefix))
        val nav = fct.map(fct => NavBarMaker.h2(fct.name,fct.name)).reduceOption(_ +"\n"+ _).getOrElse("")
        val content = fct.map(fct => ContentMaker.article(fct.name,
            ContentMaker.h2(fct.name)+
            ContentMaker.p(fct.modifiers.doc)+
            ContentMaker.arguments(fct.arguments.map(a => a.typ.toString()+" " + a.name).reduceOption(_+"<br/>"+_).getOrElse(""))
        )).reduceOption(_ +"\n"+ _).getOrElse("")

        template.replace("$title", prefix)
                .replace("$navbar", nav)
                .replace("$content", content)
    }
}

object NavBarMaker{
    def h1(text: String, section: String)={
        f"""<li class="nav-item section-title"><a class="nav-link scrollto active" href="#${section}"><span class="theme-icon-holder me-2"><i class="fas fa-map-signs"></i></span>${text}</a></li>"""
    }
    def h2(text: String, section: String)={
        f"""<li class="nav-item"><a class="nav-link scrollto" href="#${section}">${text}</a></li>"""
    }
}

object ContentMaker{
    def h1(text: String, lastupdate: String)={
        f"""<h1 class="docs-heading">${text} <span class="docs-time">Last updated: ${lastupdate}</span></h1>"""
    }

    def h2(text: String)={
        f"""<h2 class="section-heading">${text}</h2>"""
    }

    def p(text: String)={
        f"""<section class="docs-intro"><p>${text}</p></section>"""
    }

    def article(section: String, content: String)={
        f"""<article class="docs-article" id="${section}">$content</article>"""
    }
    def arguments(content: String)={
        f"""<div class="alert alert-secondary" role="alert">$content</div>"""
    }
}