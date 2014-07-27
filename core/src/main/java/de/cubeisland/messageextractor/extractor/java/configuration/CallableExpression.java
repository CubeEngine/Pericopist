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

import de.cubeisland.messageextractor.extractor.java.configuration.CallableSignatureType.CallableSignatureTypeUsage;
import spoon.reflect.reference.CtExecutableReference;
import spoon.reflect.reference.CtTypeReference;

public abstract class CallableExpression extends TranslatableExpression
{
    private CallableSignatureType[] signature;

    public CallableSignatureType[] getSignature()
    {
        return signature;
    }

    @XmlElementWrapper(name = "signature")
    @XmlElement(name = "type")
    public void setSignature(CallableSignatureType[] signature)
    {
        this.signature = signature;
    }

    public int getSingularIndex()
    {
        return this.getFirstIndexOf(CallableSignatureTypeUsage.SINGULAR);
    }

    public int getPluralIndex()
    {
        return this.getFirstIndexOf(CallableSignatureTypeUsage.PLURAL);
    }

    public boolean hasPlural()
    {
        return this.getPluralIndex() > -1;
    }

    private int getFirstIndexOf(CallableSignatureTypeUsage usage)
    {
        int i = 0;
        for(CallableSignatureType type : this.signature)
        {
            if(type.getUsage().equals(usage))
            {
                return i;
            }
            i++;
        }
        return -1;
    }

    protected boolean matchesSignature(CtExecutableReference<?> executable)
    {
        List<CtTypeReference<?>> parameters = executable.getParameterTypes();

        if(parameters.size() != this.getSignature().length)
        {
            return false;
        }

        for(int i = 0; i < parameters.size(); i++)
        {
            if(!parameters.get(i).toString().equals(this.getSignature()[i].getType()))
            {
                return false;
            }
        }
        return true;
    }
}
