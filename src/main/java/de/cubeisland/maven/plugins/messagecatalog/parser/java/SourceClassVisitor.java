package de.cubeisland.maven.plugins.messagecatalog.parser.java;

import de.cubeisland.maven.plugins.messagecatalog.parser.Occurrence;
import de.cubeisland.maven.plugins.messagecatalog.parser.TranslatableMessage;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.ImportDeclaration;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

class SourceClassVisitor extends ASTVisitor
{

    private final HashMap<String, Set<String>> annotations = new HashMap<String, Set<String>>()
    {
        {
            put("de.cubeisland.cubeengine.core.command.reflected.Command", new HashSet<String>()
            {
                {
                    add("desc");
                    add("usage");
                }
            });
        }
    };

    private final Set<String> methods = new HashSet<String>()
    {
        {
            add("sendTranslated");
            add("getTranslation");
            add("translate");
        }
    };

    private final CompilationUnit compilationUnit;
    private final String basePackage;
    private final Map<String, TranslatableMessage> messages;
    private final Map<String, String> importedClasses;

    public SourceClassVisitor(CompilationUnit compilationUnit, String basePackage)
    {
        this.compilationUnit = compilationUnit;
        this.basePackage = basePackage;
        this.messages = new HashMap<String, TranslatableMessage>();
        this.importedClasses = new HashMap<String, String>();
    }

    public Set<TranslatableMessage> getMessages()
    {
        return new HashSet<TranslatableMessage>(this.messages.values());
    }

    private void addMessage(String string, Occurrence occurrence)
    {
        TranslatableMessage message = this.messages.get(string);
        if (message == null)
        {
            this.messages.put(string, new TranslatableMessage(string, occurrence));
        }
        else
        {
            message.addOccurrence(occurrence);
        }
    }

    @Override
    public boolean visit(ImportDeclaration node)
    {
        System.out.println(node.getName());
        return super.visit(node);
    }

    //    @Override
//    public void visit(ImportDeclaration n, File file)
//    {
//        if (!n.isStatic() && !n.isAsterisk())
//        {
//            final NameExpr nameExpr = n.getName();
//            final String fqcn = nameExpr.toString();
//            if (fqcn.startsWith(basePackage))
//            {
//                this.importedClasses.put(nameExpr.getName(), fqcn);
//            }
//        }
//        super.visit(n);
//    }
//
//    @Override
//    public void visit(NormalAnnotationExpr n, File file)
//    {
//        Set<String> fields = this.annotations.get(n.getName().getName());
//        if (fields != null)
//        {
//            for (MemberValuePair pair : n.getPairs())
//            {
//                if (fields.contains(pair.getName()))
//                {
//                    Expression expr = pair.getValue();
//                    if (expr instanceof StringLiteralExpr)
//                    {
//                        this.addMessage(((StringLiteralExpr)expr).getValue(), new Occurrence(file, expr.getBeginLine()));
//                    }
//                }
//            }
//        }
//        super.visit(n, file);
//    }
//
//    @Override
//    public void visit(MethodCallExpr n, File file)
//    {
//        if (this.methods.contains(n.getName()))
//        {
//            List<Expression> args = n.getArgs();
//            if (args.size() > 0)
//            {
//                Expression expr = args.get(0);
//                if (expr instanceof StringLiteralExpr)
//                {
//                    this.addMessage(((StringLiteralExpr)expr).getValue(), new Occurrence(file, expr.getBeginLine()));
//                }
//            }
//        }
//        super.visit(n, file);
//    }
}
