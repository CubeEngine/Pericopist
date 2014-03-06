package de.cubeisland.maven.plugins.messagecatalog.parser.java;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.ImportDeclaration;
import org.eclipse.jdt.core.dom.InfixExpression;
import org.eclipse.jdt.core.dom.MarkerAnnotation;
import org.eclipse.jdt.core.dom.MemberValuePair;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.NormalAnnotation;
import org.eclipse.jdt.core.dom.PackageDeclaration;
import org.eclipse.jdt.core.dom.SingleMemberAnnotation;
import org.eclipse.jdt.core.dom.StringLiteral;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import de.cubeisland.maven.plugins.messagecatalog.message.MessageStore;
import de.cubeisland.maven.plugins.messagecatalog.message.Occurrence;
import de.cubeisland.maven.plugins.messagecatalog.parser.java.config.JavaExtractorConfiguration;
import de.cubeisland.maven.plugins.messagecatalog.parser.java.config.TranslatableAnnotation;
import de.cubeisland.maven.plugins.messagecatalog.parser.java.config.TranslatableMethod;
import de.cubeisland.maven.plugins.messagecatalog.util.Misc;

class SourceClassVisitor extends ASTVisitor
{
    private final JavaExtractorConfiguration configuration;
    private final MessageStore messageManager;
    private final CompilationUnit compilationUnit;
    private final File file;

    private String packageName;
    private Set<String> normalImports;
    private Set<String> onDemandImports;

    public SourceClassVisitor(JavaExtractorConfiguration configuration, MessageStore messageManager, CompilationUnit compilationUnit, File file)
    {
        this.configuration = configuration;
        this.messageManager = messageManager;
        this.compilationUnit = compilationUnit;
        this.file = file;

        this.normalImports = new HashSet<String>();
        this.onDemandImports = new HashSet<String>();
        this.onDemandImports.add("java.lang");
    }

    private int getLine(ASTNode node)
    {
        return this.compilationUnit.getLineNumber(node.getStartPosition());
    }

    private TranslatableAnnotation getTranslatableAnnotation(String simpleName)
    {
        TranslatableAnnotation annotation;
        for (String normal : this.normalImports)
        {
            if (normal.endsWith(simpleName))
            {
                annotation = this.configuration.getAnnotation(normal);
                if (annotation != null && annotation.getSimpleName().equals(simpleName))
                {
                    return annotation;
                }
            }
        }
        for (String onDemand : this.onDemandImports)
        {
            annotation = this.configuration.getAnnotation(onDemand + "." + simpleName);
            if (annotation != null)
            {
                return annotation;
            }
        }
        return this.configuration.getAnnotation(this.packageName + "." + simpleName);
    }

    @Override
    public boolean visit(PackageDeclaration node)
    {
        this.packageName = node.getName().getFullyQualifiedName();
        return super.visit(node);
    }

    @Override
    public boolean visit(ImportDeclaration node)
    {
        if (!node.isStatic())
        {
            if (node.isOnDemand())
            {
                this.onDemandImports.add(node.getName().getFullyQualifiedName());
            }
            else
            {
                this.normalImports.add(node.getName().getFullyQualifiedName());
            }
        }
        return super.visit(node);
    }

    @Override
    public boolean visit(NormalAnnotation node)
    {
        TranslatableAnnotation annotation = this.getTranslatableAnnotation(node.getTypeName().getFullyQualifiedName());
        if (annotation != null)
        {
            for (Object o : node.values())
            {
                if (!(o instanceof MemberValuePair))
                {
                    continue;
                }
                MemberValuePair pair = (MemberValuePair)o;

                if (annotation.hasField(pair.getName().getFullyQualifiedName()))
                {
                    Expression expr = pair.getValue();
                    if (expr instanceof StringLiteral)
                    {
                        this.messageManager.addMessage(((StringLiteral)expr).getLiteralValue(), null, new Occurrence(Misc.getRelativizedFile(this.configuration.getDirectory(), this.file), this.getLine(expr)));
                    }
                }
            }
        }
        return super.visit(node);
    }

    @Override
    public boolean visit(SingleMemberAnnotation node)
    {
        TranslatableAnnotation annotation = this.getTranslatableAnnotation(node.getTypeName().getFullyQualifiedName());
        if (annotation != null && annotation.hasField("value"))
        {
            Expression expr = node.getValue();
            if (expr instanceof StringLiteral)
            {
                this.messageManager.addMessage(((StringLiteral)expr).getLiteralValue(), null, new Occurrence(Misc.getRelativizedFile(this.configuration.getDirectory(), this.file), this.getLine(expr)));
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
        TranslatableMethod method = this.configuration.getMethod(node.getName().getIdentifier());
        if (method != null)
        {
            String singular = null;
            String plural = null;

            List args = node.arguments();
            if (args.size() > method.getSingularIndex())
            {
                Expression expr = (Expression)args.get(method.getSingularIndex());
                if (expr instanceof StringLiteral)
                {
                    singular = ((StringLiteral)expr).getLiteralValue();
                }
                else if (expr instanceof InfixExpression)
                {
                    singular = this.getString((InfixExpression)expr);
                }
            }
            if (method.hasPlural() && args.size() > method.getPluralIndex())
            {
                Expression expr = (Expression)args.get(method.getPluralIndex());
                if (expr instanceof StringLiteral)
                {
                    plural = ((StringLiteral)expr).getLiteralValue();
                }
                else if (expr instanceof InfixExpression)
                {
                    plural = this.getString((InfixExpression)expr);
                }
            }

            if (singular != null)
            {
                this.messageManager.addMessage(singular, plural, new Occurrence(Misc.getRelativizedFile(this.configuration.getDirectory(), this.file), this.getLine(node)));
            }
        }

        return super.visit(node);
    }

    private String getString(InfixExpression expr)
    {
        StringBuilder value = new StringBuilder(2 + expr.extendedOperands().size());
        List<Expression> expressions = new ArrayList<Expression>(2 + expr.extendedOperands().size());

        expressions.add(expr.getLeftOperand());
        expressions.add(expr.getRightOperand());
        for (Object o : expr.extendedOperands())
        {
            if (o instanceof Expression)
            {
                expressions.add((Expression)o);
            }
        }

        for (Expression e : expressions)
        {
            if (e instanceof StringLiteral)
            {
                value.append(((StringLiteral)e).getLiteralValue());
            }
            else if (e instanceof InfixExpression)
            {
                value.append(this.getString((InfixExpression)e));
            }
            else
            {
                return null;
            }
        }

        return value.toString();
    }
}
