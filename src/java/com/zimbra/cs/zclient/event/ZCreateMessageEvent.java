/*
 * ***** BEGIN LICENSE BLOCK *****
 * Version: MPL 1.1
 *
 * The contents of this file are subject to the Mozilla Public License
 * Version 1.1 ("License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://www.zimbra.com/license
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See
 * the License for the specific language governing rights and limitations
 * under the License.
 *
 * The Original Code is: Zimbra Collaboration Suite Server.
 *
 * The Initial Developer of the Original Code is Zimbra, Inc.
 * Portions created by Zimbra are Copyright (C) 2006 Zimbra, Inc.
 * All Rights Reserved.
 *
 * Contributor(s):
 *
 * ***** END LICENSE BLOCK *****
 */

package com.zimbra.cs.zclient.event;

import com.zimbra.soap.Element;
import com.zimbra.common.service.ServiceException;
import com.zimbra.common.soap.MailConstants;
import com.zimbra.cs.zclient.ZItem;
import com.zimbra.cs.zclient.ZEmailAddress;
import com.zimbra.cs.zclient.ZSoapSB;

import java.util.List;
import java.util.ArrayList;

public class ZCreateMessageEvent implements ZCreateItemEvent {

    protected Element mMessageEl;

    public ZCreateMessageEvent(Element e) throws ServiceException {
        mMessageEl = e;
    }

    /**
     * @return id
     * @throws com.zimbra.common.service.ServiceException on error
     */
    public String getId() throws ServiceException {
        return mMessageEl.getAttribute(MailConstants.A_ID);
    }

    /**
     * @param defaultValue value to return if unchanged
     * @return new flags or default value if flags didn't change
     */
    public String getFlags(String defaultValue) {
        return mMessageEl.getAttribute(MailConstants.A_FLAGS, defaultValue);
    }

    /**
     * @param defaultValue value to return if unchanged
     * @return new tags or default value if tags didn't change
     */
    public String getTagIds(String defaultValue) {
        return mMessageEl.getAttribute(MailConstants.A_TAGS, defaultValue);
    }

    /**
     * @param defaultValue value to return if unchanged
     * @return new message count, or defaultValue if unchanged
     * @throws com.zimbra.common.service.ServiceException on error
     */
    public int getSize(int defaultValue) throws ServiceException {
        return (int) mMessageEl.getAttributeLong(MailConstants.A_SIZE, defaultValue);
    }

    /**
     * @param defaultValue value to return if unchanged
     * @return new folder id or defaultValue if unchanged
     */
    public String getFolderId(String defaultValue) {
        return mMessageEl.getAttribute(MailConstants.A_FOLDER, defaultValue);
    }

    /**
     * @param defaultValue value to return if unchanged
     * @return new conv id or defaultValue if unchanged
     */
    public String getConversationId(String defaultValue) {
        return mMessageEl.getAttribute(MailConstants.A_CONV_ID, defaultValue);
    }

    /**
     * @param defaultValue value to return if unchanged
     * @return new subject or defaultValue if unchanged
     */
    public String getSubject(String defaultValue) {
        Element sub = mMessageEl.getOptionalElement(MailConstants.E_SUBJECT);
        return sub == null ? defaultValue : sub.getText();
    }

    /**
     * @param defaultValue value to return if unchanged
     * @return new fragment or defaultValue if unchanged
     */
    public String getFragment(String defaultValue) {
        Element frag = mMessageEl.getOptionalElement(MailConstants.E_FRAG);
        return frag == null ? defaultValue : frag.getText();
    }

    /**
     * @param defaultValue value to return if unchanged
     * @return new date or defaultValue if unchanged
     * @throws com.zimbra.common.service.ServiceException on error
     */
    public long getReceivedDate(long defaultValue) throws ServiceException {
        return mMessageEl.getAttributeLong(MailConstants.A_DATE, defaultValue);
    }

    /**
     * @param defaultValue value to return if unchanged
     * @return new date or defaultValue if unchanged
     * @throws com.zimbra.common.service.ServiceException on error
     */
    public long getSentDate(long defaultValue) throws ServiceException {
        return mMessageEl.getAttributeLong(MailConstants.A_SENT_DATE, defaultValue);
    }

    /**
     * @param defaultValue value to return if unchanged
     * @return grants or defaultValue if unchanged
     * @throws com.zimbra.common.service.ServiceException on error
     */
    public List<ZEmailAddress> getEmailAddresses(List<ZEmailAddress> defaultValue) throws ServiceException {
        List<ZEmailAddress> result  = null;
        for (Element emailEl: mMessageEl.listElements(MailConstants.E_EMAIL)) {
            if (result == null) result = new ArrayList<ZEmailAddress>();
            result.add(new ZEmailAddress(emailEl));
        }
        return result == null ? defaultValue : result;
    }

    public String toString() {
        try {
            ZSoapSB sb = new ZSoapSB();
            sb.beginStruct();
            sb.add("id", getId());
            if (getFolderId(null) != null) sb.add("folderId", getFolderId(null));            
            if (getConversationId(null) != null) sb.add("conversationId", getConversationId(null));
            if (getFlags(null) != null) sb.add("flags", getFlags(null));
            if (getTagIds(null) != null) sb.add("tags", getTagIds(null));
            if (getSubject(null) != null) sb.add("subject", getSubject(null));
            if (getFragment(null) != null) sb.add("fragment", getFragment(null));
            if (getSize(-1) != -1) sb.add("size", getSize(-1));
            if (getReceivedDate(-1) != -1) sb.add("receivedDate", getReceivedDate(-1));
            if (getSentDate(-1) != -1) sb.add("sentDate", getSentDate(-1));
            List<ZEmailAddress> addrs = getEmailAddresses(null);
            if (addrs != null) sb.add("addresses", addrs, false, false);
            sb.endStruct();
            return sb.toString();
        } catch (ServiceException se) {
            return "";
        }
    }

    public ZItem getItem() throws ServiceException {
        return null;
    }
}
