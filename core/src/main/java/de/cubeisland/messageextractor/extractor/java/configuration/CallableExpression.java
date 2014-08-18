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
package de.cubeisland.messageextractor.extractor.java.configuration;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;

import de.cubeisland.messageextractor.exception.ConfigurationException;
import de.cubeisland.messageextractor.extractor.java.configuration.CallableSignatureType.CallableSignatureTypeUsage;
import spoon.reflect.reference.CtExecutableReference;
import spoon.reflect.reference.CtTypeReference;

/**
 * This TranslatableExpression describes callable expressions
 */
public abstract class CallableExpression extends TranslatableExpression
{
    private CallableSignatureType[] signature;

    /**
     * This method returns the signature of the callable expression
     *
     * @return signature
     */
    public CallableSignatureType[] getSignature()
    {
        return signature;
    }

    /**
     * This method sets the signature of the callable expression
     *
     * @param signature signature
     */
    @XmlElementWrapper(name = "signature")
    @XmlElement(name = "type")
    public void setSignature(CallableSignatureType... signature)
    {
        this.signature = signature;
    }

    /**
     * This method returns the index of the singular parameter
     *
     * @return index of singular parameter
     */
    public int getSingularIndex()
    {
        return this.getFirstIndexOf(CallableSignatureTypeUsage.SINGULAR);
    }

    /**
     * This method returns the index of the plural parameter
     *
     * @return index of plural parameter
     */
    public int getPluralIndex()
    {
        return this.getFirstIndexOf(CallableSignatureTypeUsage.PLURAL);
    }

    /**
     * This method checks whether the callable expression has a plural parameter
     *
     * @return if a plural parameter exists
     */
    @Override
    public boolean hasPlural()
    {
        return this.getPluralIndex() > -1;
    }

    /**
     * {@inheritDoc}
     *
     * @throws ConfigurationException if name, signature or a type or usage of a signature type is null. Furthermore if no singular parameter exists.
     */
    @Override
    public void validate() throws ConfigurationException
    {
        super.validate();

        if (this.getSignature() == null)
        {
            throw new ConfigurationException("A callable expression needs a signature. Specify it with a signature tag.");
        }

        for (CallableSignatureType type : this.getSignature())
        {
            if (type.getType() == null)
            {
                throw new ConfigurationException("A signature type can't be null.");
            }

            if (type.getUsage() == null)
            {
                throw new ConfigurationException("One of the signature types from '" + this.getName() + "' has an invalid use-as attribute.");
            }
        }

        if (this.getSingularIndex() < 0)
        {
            throw new ConfigurationException("One of the signature types needs singular as the use-as attribute value.");
        }
    }

    /**
     * This method returns the first index of the specified CallableSignatureTypeUsage instance.
     *
     * @param usage usage
     *
     * @return index of the specified usage
     */
    private int getFirstIndexOf(CallableSignatureTypeUsage usage)
    {
        int i = 0;
        for (CallableSignatureType type : this.signature)
        {
            if (type.getUsage().equals(usage))
            {
                return i;
            }
            i++;
        }
        return -1;
    }

    /**
     * This method checks whether the specified executable matches the signature
     *
     * @param executable CtExecutableReference instance
     *
     * @return whether the executable matches the signature
     */
    protected boolean matchesSignature(CtExecutableReference<?> executable)
    {
        List<CtTypeReference<?>> parameters = executable.getParameterTypes();

        if (parameters.size() != this.getSignature().length)
        {
            return false;
        }

        for (int i = 0; i < parameters.size(); i++)
        {
            if (!parameters.get(i).toString().equals(this.getSignature()[i].getType()))
            {
                return false;
            }
        }
        return true;
    }

    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder(this.getName());

        builder.append('(');
        for (CallableSignatureType signatureType : this.getSignature())
        {
            builder.append(signatureType.getType());
            builder.append(", ");
        }
        builder.delete(builder.length() - 2, builder.length());
        builder.append(')');

        return builder.toString();
    }
}
