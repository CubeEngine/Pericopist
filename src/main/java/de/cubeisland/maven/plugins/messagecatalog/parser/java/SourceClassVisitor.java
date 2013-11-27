package de.cubeisland.maven.plugins.messagecatalog.parser.java;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import de.cubeisland.maven.plugins.messagecatalog.parser.Occurrence;
import de.cubeisland.maven.plugins.messagecatalog.parser.TranslatableMessage;
import japa.parser.ast.ImportDeclaration;
import japa.parser.ast.expr.Expression;
import japa.parser.ast.expr.MemberValuePair;
import japa.parser.ast.expr.MethodCallExpr;
import japa.parser.ast.expr.NameExpr;
import japa.parser.ast.expr.NormalAnnotationExpr;
import japa.parser.ast.expr.StringLiteralExpr;
import japa.parser.ast.visitor.VoidVisitorAdapter;

class SourceClassVisitor extends VoidVisitorAdapter<File>
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

    private final String basePackage;
    private final Map<String, TranslatableMessage> messages;
    private final Map<String, String> importedClasses;

    public SourceClassVisitor(String basePackage)
    {
        this.basePackage = basePackage;
        this.messages = new HashMap<String, TranslatableMessage>();
        this.importedClasses = new HashMap<String, String>();
    }

    public Map<String, TranslatableMessage> getMessages()
    {
        return this.messages;
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
    public void visit(ImportDeclaration n, File file)
    {
        if (!n.isStatic() && !n.isAsterisk())
        {
            final NameExpr nameExpr = n.getName();
            final String fqcn = nameExpr.toString();
            if (fqcn.startsWith(basePackage))
            {
                this.importedClasses.put(nameExpr.getName(), fqcn);
            }
        }
        super.visit(n, file);
    }

    @Override
    public void visit(NormalAnnotationExpr n, File file)
    {
        Set<String> fields = this.annotations.get(n.getName().getName());
        if (fields != null)
        {
            for (MemberValuePair pair : n.getPairs())
            {
                if (fields.contains(pair.getName()))
                {
                    Expression expr = pair.getValue();
                    if (expr instanceof StringLiteralExpr)
                    {
                        this.addMessage(((StringLiteralExpr)expr).getValue(), new Occurrence(file, expr.getBeginLine()));
                    }
                }
            }
        }
        super.visit(n, file);
    }

    @Override
    public void visit(MethodCallExpr n, File file)
    {
        if (this.methods.contains(n.getName()))
        {
            List<Expression> args = n.getArgs();
            if (args.size() > 0)
            {
                Expression expr = args.get(0);
                if (expr instanceof StringLiteralExpr)
                {
                    this.addMessage(((StringLiteralExpr)expr).getValue(), new Occurrence(file, expr.getBeginLine()));
                }
            }
        }
        super.visit(n, file);
    }
}
