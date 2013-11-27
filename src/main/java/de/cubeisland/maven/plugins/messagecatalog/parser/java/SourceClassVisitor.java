package de.cubeisland.maven.plugins.messagecatalog.parser.java;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.ImportDeclaration;
import org.eclipse.jdt.core.dom.MarkerAnnotation;
import org.eclipse.jdt.core.dom.MemberValuePair;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.Name;
import org.eclipse.jdt.core.dom.NormalAnnotation;
import org.eclipse.jdt.core.dom.SingleMemberAnnotation;
import org.eclipse.jdt.core.dom.StringLiteral;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import de.cubeisland.maven.plugins.messagecatalog.parser.Occurrence;
import de.cubeisland.maven.plugins.messagecatalog.parser.TranslatableMessage;

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
    private final File file;
    private final String basePackage;
    private final Map<String, TranslatableMessage> messages;
    private final Map<String, String> importedClasses;

    public SourceClassVisitor(CompilationUnit compilationUnit, File file, String basePackage)
    {
        this.compilationUnit = compilationUnit;
        this.file = file;
        this.basePackage = basePackage;
        this.messages = new HashMap<String, TranslatableMessage>();
        this.importedClasses = new HashMap<String, String>();
    }

    public Set<TranslatableMessage> getMessages()
    {
        return new HashSet<TranslatableMessage>(this.messages.values());
    }

    private int getLine(ASTNode node)
    {
        return this.compilationUnit.getLineNumber(node.getStartPosition());
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
        if(!node.isStatic() && !node.isOnDemand())
        {
            Name name = node.getName();
            String fqcn = name.getFullyQualifiedName();

            if(fqcn.startsWith(basePackage))
            {
                int dotIndex = name.getFullyQualifiedName().lastIndexOf('.');
                this.importedClasses.put(name.getFullyQualifiedName().substring(dotIndex + 1), name.getFullyQualifiedName());
            }
        }
        return super.visit(node);
    }

    @Override
    public boolean visit(NormalAnnotation node)
    {
        Set<String> fields = this.annotations.get(node.getTypeName().getFullyQualifiedName());
        if (fields != null)
        {
            for(Object o : node.values())
            {
                if(!(o instanceof MemberValuePair))
                {
                    continue;
                }
                MemberValuePair pair = (MemberValuePair) o;

                if(fields.contains(pair.getName()))
                {
                    Expression expr = pair.getValue();
                    if(expr instanceof StringLiteral)
                    {
                        this.addMessage(((StringLiteral)expr).getLiteralValue(), new Occurrence(file, this.getLine(expr)));
                    }
                }
            }
        }
        return super.visit(node);
    }

    @Override
    public boolean visit(SingleMemberAnnotation node)
    {
        Set<String> fields = this.annotations.get(node.getTypeName().getFullyQualifiedName());
        if(fields != null)
        {
            if(fields.contains("value"))
            {
                Expression expr = node.getValue();
                if(expr instanceof StringLiteral)
                {
                    this.addMessage(((StringLiteral)expr).getLiteralValue(), new Occurrence(this.file, this.getLine(expr)));
                }
            }
        }
        return super.visit(node);
    }

    @Override
    public boolean visit(MarkerAnnotation node)
    {
        /*
         * TODO Have a look at it
         * it seems that also NormalAnnotations are identified as MarkerAnnotations
         * whether they just have default values!
         * Maybe one has to catch it with the annotation declaration.
         * what is when the annotation is from another project?
         */
        return super.visit(node);
    }

    @Override
    public boolean visit(MethodInvocation node)
    {
        if(this.methods.contains(node.getName().getIdentifier()))
        {
            List args = node.arguments();
            if(args.size() > 0)
            {
                Expression expr = (Expression) args.get(0);
                if(expr instanceof StringLiteral)
                {
                    this.addMessage(((StringLiteral)expr).getLiteralValue(), new Occurrence(file, this.getLine(expr)));
                }
            }
        }
        return super.visit(node);
    }
}
