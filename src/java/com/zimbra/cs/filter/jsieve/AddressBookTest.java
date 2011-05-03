/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2004, 2005, 2006, 2007, 2008, 2009, 2010, 2011 Zimbra, Inc.
 *
 * The contents of this file are subject to the Zimbra Public License
 * Version 1.3 ("License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.zimbra.com/license.
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
 * ***** END LICENSE BLOCK *****
 */
package com.zimbra.cs.filter.jsieve;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;

import org.apache.jsieve.Argument;
import org.apache.jsieve.Arguments;
import org.apache.jsieve.SieveContext;
import org.apache.jsieve.StringListArgument;
import org.apache.jsieve.TagArgument;
import org.apache.jsieve.exception.SieveException;
import org.apache.jsieve.exception.SyntaxException;
import org.apache.jsieve.mail.MailAdapter;
import org.apache.jsieve.tests.AbstractTest;

import com.zimbra.common.mime.InternetAddress;
import com.zimbra.common.service.ServiceException;
import com.zimbra.common.util.ZimbraLog;
import com.zimbra.cs.filter.ZimbraMailAdapter;
import com.zimbra.cs.mailbox.Mailbox;

/**
 * @since Nov 11, 2004
 */
public final class AddressBookTest extends AbstractTest {
    private static final String IN = ":in";
    private static final String CONTACTS = "contacts";
    private static final String GAL = "GAL";

    @Override
    protected boolean executeBasic(MailAdapter mail, Arguments arguments, SieveContext context) throws SieveException {
        String comparator = null;
        ListIterator<Argument> argumentsIter = arguments.getArgumentList().listIterator();

        // First argument MUST be a tag of ":in"
        if (argumentsIter.hasNext()) {
            Object argument = argumentsIter.next();
            if (argument instanceof TagArgument) {
                String tag = ((TagArgument) argument).getTag();
                if (tag.equals(IN)) {
                    comparator = tag;
                } else {
                    throw new SyntaxException("Found unexpected: \"" + tag + "\"");
                }
            }
        }
        if (null == comparator) {
            throw new SyntaxException("Expecting \":in\"");
        }
        // Second argument MUST be header names
        String[] headers = null;
        if (argumentsIter.hasNext()) {
            Object argument = argumentsIter.next();
            if (argument instanceof StringListArgument) {
                StringListArgument strList = (StringListArgument) argument;
                headers = new String[strList.getList().size()];
                for (int i=0; i< headers.length; i++) {
                    headers[i] = strList.getList().get(i);
                }
            }
        }
        if (headers == null) {
            throw new SyntaxException("No headers are found");
        }
        Set<String> abooks = null;
        // Third argument MUST be either contacts or GAL
        if (argumentsIter.hasNext()) {
            Object argument = argumentsIter.next();
            if (argument instanceof StringListArgument) {
                StringListArgument strList = (StringListArgument) argument;
                abooks = new HashSet<String>();
                for (int i=0; i< strList.getList().size(); i++) {
                    String abookName = strList.getList().get(i);
                    if (!CONTACTS.equals(abookName) && !GAL.equals(abookName)) {
                        throw new SyntaxException("Unknown address book name: " + abookName);
                    }
                    // eliminate duplicates by adding it to the set
                    abooks.add(abookName);
                }
            }
        }
        if (abooks == null || abooks.isEmpty()) {
            throw new SyntaxException("Expecting address book name(s)");
        }
        // There MUST NOT be any further arguments
        if (argumentsIter.hasNext()) {
            throw new SyntaxException("Found unexpected argument(s)");
        }
        if (!(mail instanceof ZimbraMailAdapter)) {
            return false;
        }
        return test(mail, headers, abooks);
    }

    private boolean test(MailAdapter mail, String[] headers, Set<String> abooks) throws SieveException {
        Mailbox mbox = ((ZimbraMailAdapter) mail).getMailbox();
        if (abooks.contains(CONTACTS)) {
            List<InternetAddress> addrs = new ArrayList<InternetAddress>();
            // get values for header that should contains address, like From, To, etc.
            for (String header : headers) {
                // each header may contain multiple values; e.g., To: may contain many recipients
                for (String value : mail.getHeader(header)) {
                    addrs.add(new InternetAddress(value));
                }
            }
            try {
                return mbox.existsInContacts(addrs);
            } catch (ServiceException e) {
                ZimbraLog.filter.error("Failed to process AddressBookTest", e);
            }
        }
        //TODO searching other address database like GAL
        return false;
    }

    @Override
    protected void validateArguments(Arguments arguments, SieveContext context) {
    }
}
