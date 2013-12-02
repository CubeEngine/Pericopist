package de.cubeisland.maven.plugins.messagecatalog.parser.java;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.InfixExpression;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.StringLiteral;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import de.cubeisland.maven.plugins.messagecatalog.parser.Occurrence;
import de.cubeisland.maven.plugins.messagecatalog.parser.TranslatableMessage;
import de.cubeisland.maven.plugins.messagecatalog.parser.java.translatables.TranslatableMethod;

class SourceClassVisitor extends ASTVisitor
{
//    private final JavaSourceParser parser;
    private final JavaParserConfiguration configuration;
    private final CompilationUnit compilationUnit;
    private final File file;

    private final Map<String, TranslatableMessage> messages;
//    private final Map<String, String> importedClasses;

    public SourceClassVisitor(JavaParserConfiguration configuration, CompilationUnit compilationUnit, File file)
    {
//        this.parser = parser;
        this.configuration = configuration;
        this.compilationUnit = compilationUnit;
        this.file = file;

        this.messages = new HashMap<String, TranslatableMessage>();
//        this.importedClasses = new HashMap<String, String>();
    }

    public Set<TranslatableMessage> getMessages()
    {
        return new HashSet<TranslatableMessage>(this.messages.values());
    }

    private int getLine(ASTNode node)
    {
        return this.compilationUnit.getLineNumber(node.getStartPosition());
    }

    private void addMessage(String singular, String plural, Occurrence occurrence) // TODO add plural
    {
        TranslatableMessage message = this.messages.get(singular);
        if (message == null)
        {
            this.messages.put(singular, new TranslatableMessage(singular, occurrence));
        }
        else
        {
            message.addOccurrence(occurrence);
        }
    }

//    @Override
//    public boolean visit(ImportDeclaration node)
//    {
//        if(!node.isStatic() && !node.isOnDemand())
//        {
//            Name name = node.getName();
//            String fqcn = name.getFullyQualifiedName();
//
//            if(this.parser.startsWithBasePackage(fqcn))
//            {
//                int dotIndex = name.getFullyQualifiedName().lastIndexOf('.');
//                this.importedClasses.put(name.getFullyQualifiedName().substring(dotIndex + 1), name.getFullyQualifiedName());
//            }
//        }
//        return super.visit(node);
//    }
//
//    @Override
//    public boolean visit(NormalAnnotation node)
//    {
//        String annotationName = node.getTypeName().getFullyQualifiedName();
//        if (this.parser.isTranslatableAnnotation(annotationName))
//        {
//            for(Object o : node.values())
//            {
//                if(!(o instanceof MemberValuePair))
//                {
//                    continue;
//                }
//                MemberValuePair pair = (MemberValuePair) o;
//
//                if(this.parser.isTranslatableAnnotationField(annotationName, pair.getName().getFullyQualifiedName()))
//                {
//                    Expression expr = pair.getValue();
//                    if(expr instanceof StringLiteral)
//                    {
//                        this.addMessage(((StringLiteral)expr).getLiteralValue(), new Occurrence(file, this.getLine(expr)));
//                    }
//                }
//            }
//        }
//        return super.visit(node);
//    }
//
//    @Override
//    public boolean visit(SingleMemberAnnotation node)
//    {
//        if(this.parser.isTranslatableAnnotationField(node.getTypeName().getFullyQualifiedName(), "value"))
//        {
//            Expression expr = node.getValue();
//            if(expr instanceof StringLiteral)
//            {
//                this.addMessage(((StringLiteral)expr).getLiteralValue(), new Occurrence(this.file, this.getLine(expr)));
//            }
//        }
//        return super.visit(node);
//    }
//
//    @Override
//    public boolean visit(MarkerAnnotation node)
//    {
//        /*
//         * TODO Have a look at it
//         * it seems that also NormalAnnotations are identified as MarkerAnnotations
//         * whether they just have default values!
//         * Maybe one has to catch it with the annotation declaration.
//         * what is when the annotation is from another project?
//         */
//        return super.visit(node);
//    }

    @Override
    public boolean visit(MethodInvocation node)
    {
        TranslatableMethod method = this.configuration.getMethod(node.getName().getIdentifier());
        if (method != null)
        {
            String singular = null;
            String plural = null;

            List args = node.arguments();
            if(args.size() > method.getSingularIndex())
            {
                Expression expr = (Expression) args.get(method.getSingularIndex());
                if (expr instanceof StringLiteral)
                {
                    singular = ((StringLiteral)expr).getLiteralValue();
                }
                else if(expr instanceof InfixExpression)
                {
                    singular = this.getString((InfixExpression) expr);
                }
            }
            if(method.hasPlural() && args.size() > method.getPluralIndex())
            {
                Expression expr = (Expression) args.get(method.getPluralIndex());
                if (expr instanceof StringLiteral)
                {
                    plural = ((StringLiteral)expr).getLiteralValue();
                }
                else if(expr instanceof InfixExpression)
                {
                    plural = this.getString((InfixExpression) expr);
                }
            }

            if(singular != null)
            {
                this.addMessage(singular, plural, new Occurrence(file, this.getLine(node)));
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
        for(Object o : expr.extendedOperands())
        {
            if(o instanceof Expression)
            {
                expressions.add((Expression)o);
            }
        }

        for(Expression e : expressions)
        {
            if (e instanceof StringLiteral)
            {
                value.append(((StringLiteral)e).getLiteralValue());
            }
            else if(e instanceof InfixExpression)
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
