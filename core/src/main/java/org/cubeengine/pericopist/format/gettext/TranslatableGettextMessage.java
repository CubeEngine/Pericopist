/*
 * The MIT License
 * Copyright © 2013 Cube Island
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
package org.cubeengine.pericopist.format.gettext;

import org.fedorahosted.tennera.jgettext.Message;

import java.util.Collection;
import java.util.List;

import org.cubeengine.pericopist.message.SourceReference;

/**
 * This gettext message is a normal one. It stores extra information like
 * translations, previous message id's and comments.
 */
class TranslatableGettextMessage extends GettextMessage
{
    private final String domain;
    private final String msgstr;
    private final List<String> msgstrPlural;

    private final String prevMsgctx;
    private final String prevMsgid;
    private final String prevMsgidPlural;

    private final Collection<String> comments;
    private final Collection<String> extractedCommentsFromGettext;
    private final List<String> gettextReferences;

    private final Collection<String> formats;
    private final boolean obsolete;
    private final Boolean allowWrap;

    /**
     * The constructor creates a new instance of this class
     *
     * @param message  jgettext {@link Message} instance which was parsed from the catalog
     * @param position position within the catalog
     */
    public TranslatableGettextMessage(Message message, int position)
    {
        super(message.getMsgctxt(), message.getMsgid(), message.getMsgidPlural(), position);

        this.domain = message.getDomain();
        this.msgstr = message.getMsgstr();
        this.msgstrPlural = message.getMsgstrPlural();

        this.prevMsgctx = message.getPrevMsgctx();
        this.prevMsgid = message.getPrevMsgid();
        this.prevMsgidPlural = message.getPrevMsgidPlural();

        this.comments = message.getComments();
        this.extractedCommentsFromGettext = message.getExtractedComments();
        this.gettextReferences = message.getSourceReferences();
        this.formats = message.getFormats();

        this.obsolete = message.isObsolete();
        this.allowWrap = message.getAllowWrap();
    }

    /**
     * This method returns the domain of this message
     *
     * @return domain
     */
    public String getDomain()
    {
        return domain;
    }

    /**
     * This method returns the singular translation of this message
     *
     * @return singular translation
     */
    public String getMsgstr()
    {
        return msgstr;
    }

    /**
     * This method returns the plural translations of this message
     *
     * @return plural translation
     */
    public List<String> getMsgstrPlural()
    {
        return msgstrPlural;
    }

    /**
     * This method returns the previous message context.
     *
     * @return previous message context
     */
    public String getPrevMsgctx()
    {
        return prevMsgctx;
    }

    /**
     * This method returns the previous message id
     *
     * @return previous message id
     */
    public String getPrevMsgid()
    {
        return prevMsgid;
    }

    /**
     * This method returns the previous plural message id
     *
     * @return previous plural message id
     */
    public String getPrevMsgidPlural()
    {
        return prevMsgidPlural;
    }

    /**
     * This method returns the comments from this message
     *
     * @return comments
     */
    public Collection<String> getComments()
    {
        return comments;
    }

    /**
     * This method returns the extracted comments from the gettext catalog.
     * <p/>
     * This won't be added to the new catalog. It's used to compare the
     * old with the new context to decide whether the catalog has changes.
     *
     * @return extracted comments from the gettext catalog
     */
    public Collection<String> getExtractedCommentsFromGettext()
    {
        return extractedCommentsFromGettext;
    }

    /**
     * This method returns the source references from the gettext catalog.
     * <p/>
     * This won't be added to the new catalog. It's used to compare the
     * old with the new source references to decide whether the catalog has changes.
     *
     * @return source references from the gettext catalog
     */
    public List<String> getGettextReferences()
    {
        return this.gettextReferences;
    }

    /**
     * This method returns the formats (flags) from the catalog
     * with the exception of the flag obsolete.
     *
     * @return formats of the message
     *
     * @see #isObsolete()
     */
    public Collection<String> getFormats()
    {
        return formats;
    }

    /**
     * This method returns the information whether this message is
     * obsolete. This flag is set if the message isn't used anymore
     * but was used in an older version of the catalog
     *
     * @return is the message obsolete
     */
    public boolean isObsolete()
    {
        return obsolete;
    }

    /**
     * This method returns whether wrapping is allowed for this message
     *
     * @return is wrapping allowed
     */
    public Boolean getAllowWrap()
    {
        return allowWrap;
    }

    @Override
    public Message toMessage()
    {
        Message message = new Message();

        message.setDomain(this.getDomain());
        message.setMsgctxt(this.getContext());
        message.setMsgid(this.getSingular());
        message.setMsgidPlural(this.getPlural());
        if (this.hasPlural())
        {
            for (int i = 0; i < this.getMsgstrPlural().size(); i++)
            {
                message.addMsgstrPlural(this.getMsgstrPlural().get(i), i);
            }
        }
        else
        {
            message.setMsgstr(this.getMsgstr());
        }

        message.setPrevMsgctx(this.getPrevMsgctx());
        message.setPrevMsgid(this.getPrevMsgid());
        message.setPrevMsgidPlural(this.getPrevMsgidPlural());

        for (String comment : this.getComments())
        {
            message.addComment(comment);
        }
        for (String extractedComment : GettextUtils.createExtractedComments(this))
        {
            message.addExtractedComment(extractedComment);
        }
        for (SourceReference sourceReference : this.getSourceReferences())
        {
            message.addSourceReference(sourceReference.getPath(), sourceReference.getLine());
        }

        for (String format : this.getFormats())
        {
            message.addFormat(format);
        }
        message.setObsolete(this.isObsolete());
        message.setAllowWrap(this.getAllowWrap());

        return message;
    }

    /**
     * This method should be called after extracting the new messages from the {@link org.cubeengine.pericopist.extractor.MessageExtractor}.
     * It checks whether this message has changes or is exactly the same as before the extracting of the source messages.
     *
     * @return whether this message has changes
     */
    public boolean hasChanges()
    {
        // 1. compare source references
        if (this.getSourceReferences().size() != this.getGettextReferences().size())
        {
            return true;
        }

        int i = 0;
        for (SourceReference reference : this.getSourceReferences())
        {
            if (!reference.toString().equals(this.getGettextReferences().get(i++)))
            {
                return true;
            }
        }

        // 2. compare extracted comments
        StringBuilder extractedCommentsGettext = new StringBuilder();
        StringBuilder extractedCommentsSource = new StringBuilder();
        for (String extractedComment : this.getExtractedCommentsFromGettext())
        {
            extractedCommentsGettext.append(extractedComment);
        }
        for (String extractedComment : GettextUtils.createExtractedComments(this))
        {
            extractedCommentsSource.append(extractedComment);
        }

        return !extractedCommentsGettext.toString().equals(extractedCommentsSource.toString());
    }

    @Override
    public boolean equals(Object o)
    {
        // just use the super method; because the additional fields of this class are just for
        // catalog creation purposes. The messages from the source code shall be added to this instances either
        return super.equals(o);
    }

    @Override
    public int hashCode()
    {
        // just use the super method!
        return super.hashCode();
    }
}
