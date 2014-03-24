/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 Phillip Schichtel, Stefan Wolf
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package de.cubeisland.messageextractor.extractor.java;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import de.cubeisland.messageextractor.extractor.java.config.JavaExtractorConfiguration;
import de.cubeisland.messageextractor.extractor.java.config.TranslatableAnnotation;
import de.cubeisland.messageextractor.extractor.java.config.TranslatableMethod;
import de.cubeisland.messageextractor.message.MessageStore;
import de.cubeisland.messageextractor.message.Occurrence;
import de.cubeisland.messageextractor.util.Misc;
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

class SourceClassVisitor extends ASTVisitor
{
    private final JavaExtractorConfiguration configuration;
    private final MessageStore messageStore;
    private final CompilationUnit compilationUnit;
    private final File file;

    private String packageName;
    private Set<String> normalImports;
    private Set<String> onDemandImports;

    public SourceClassVisitor(JavaExtractorConfiguration configuration, MessageStore messageManager, CompilationUnit compilationUnit, File file)
    {
        this.configuration = configuration;
        this.messageStore = messageManager;
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
                        this.messageStore.addMessage(((StringLiteral)expr)
                                                         .getLiteralValue(), null, new Occurrence(Misc.getRelativizedFile(this.configuration
                                                                                                                              .getDirectory(), this.file), this
                                                                                                      .getLine(expr)));
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
                this.messageStore.addMessage(((StringLiteral)expr)
                                                 .getLiteralValue(), null, new Occurrence(Misc.getRelativizedFile(this.configuration
                                                                                                                      .getDirectory(), this.file), this
                                                                                              .getLine(expr)));
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
                this.messageStore.addMessage(singular, plural, new Occurrence(Misc.getRelativizedFile(this.configuration
                                                                                                          .getDirectory(), this.file), this
                                                                                  .getLine(node)));
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