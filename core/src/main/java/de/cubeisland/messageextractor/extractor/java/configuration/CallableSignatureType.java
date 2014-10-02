/**
 * The MIT License
 * Copyright (c) 2013 Cube Island
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

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlValue;

/**
 * The CallableSignatureType class describes a type of the signature which
 * is used by the CallableExpression instances.
 *
 * @see de.cubeisland.messageextractor.extractor.java.configuration.CallableExpression
 */
@XmlRootElement(name = "type")
public class CallableSignatureType
{
    private CallableSignatureTypeUsage usage = CallableSignatureTypeUsage.NONE;
    private String type;

    /**
     * This method returns the usage of the signature type
     *
     * @return usage
     */
    public CallableSignatureTypeUsage getUsage()
    {
        return this.usage;
    }

    /**
     * This method sets the usage of the signature type
     *
     * @param usage usage
     */
    @XmlAttribute(name = "use-as")
    public void setUsage(CallableSignatureTypeUsage usage)
    {
        this.usage = usage;
    }

    /**
     * This method returns the fully qualified class name of the type
     *
     * @return class name
     */
    public String getType()
    {
        return this.type;
    }

    /**
     * This method sets the fully qualified class name of the type
     *
     * @param type class name
     */
    @XmlValue
    public void setType(String type)
    {
        this.type = type;
    }

    /**
     * The CallableSignatureTypeUsage specifies the role which the
     * type has in this program context
     */
    @XmlRootElement(name = "use-as")
    @XmlEnum
    public enum CallableSignatureTypeUsage
    {
        /**
         * This is the default value. It means that the type doesn't have a special role
         * in the messageextractor context. The type will just be used to match the signature
         * of callable expressions.
         */
        @XmlEnumValue("none")
        NONE,

        /**
         * This value means that the messageextractor tries to extract the singular message
         * from that signature type
         */
        @XmlEnumValue("singular")
        SINGULAR,

        /**
         * This value means that the messageextractor tries to extract the plural message
         * from that signature type
         */
        @XmlEnumValue("plural")
        PLURAL
    }
}
