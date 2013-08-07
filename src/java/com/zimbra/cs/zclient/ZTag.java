/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2006, 2007, 2008, 2009, 2010, 2011, 2012, 2013 Zimbra Software, LLC.
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
package com.zimbra.cs.zclient;

import java.util.HashMap;
import java.util.Map;

import com.zimbra.common.service.ServiceException;
import com.zimbra.common.soap.Element;
import com.zimbra.common.soap.MailConstants;
import com.zimbra.cs.mailbox.MailItem;
import com.zimbra.cs.zclient.event.ZModifyEvent;
import com.zimbra.cs.zclient.event.ZModifyTagEvent;
import org.json.JSONException;

public class ZTag implements Comparable, ZItem, ToZJSONObject {

    private Color mColor;
    private String mId;
    private String mName;
    private int mUnreadCount;
    private ZMailbox mMailbox;
        
    public enum Color {
        
        defaultColor(0),
        blue(1),
        cyan(2), 
        green(3),
        purple(4),
        red(5),
        yellow(6),
        pink(7),
        gray(8),
        orange(9),
        rgbColor;

        private long mValue;
        private static Map<String,Color> colorMap = new HashMap<String,Color>();

        static {
          for (Color c : Color.values())
                colorMap.put(c.toString(), c);
        }

        public long getValue() { return mValue; }

        public static Color fromString(String s) throws ServiceException {
            if (s != null) {
                s = s.toLowerCase();
                if (colorMap.containsKey(s)) {
                    return colorMap.get(s);
                }
            }
            return Color.values()[MailItem.Color.getMappedColor(s)];
        }

        public Color setRgbColor(String s) {
            mValue = new MailItem.Color(s).getValue();
            return this;
        }

        public String getRgbColor() {
            return new MailItem.Color(mValue).toString();
        }

        Color(long value) { mValue = value; }
        Color() {}
    }

    public ZTag(Element e, ZMailbox mailbox) throws ServiceException {
        mMailbox = mailbox;
        String rgb = e.getAttribute(MailConstants.A_RGB, null);
        // Server reports color or rgb attribute on mail items but not both. 
        // If rgb, map the color to the rgb value. If the attr is color, return the value as is.
        if (rgb != null) {
            mColor =  Color.rgbColor.setRgbColor(rgb);
        } else {
            String s = e.getAttribute(MailConstants.A_COLOR, "0");
            mColor = Color.values()[(byte)Long.parseLong(s)];
        }
        mId = e.getAttribute(MailConstants.A_ID);
        mName = e.getAttribute(MailConstants.A_NAME);
        mUnreadCount = (int) e.getAttributeLong(MailConstants.A_UNREAD, 0);
    }

    public void modifyNotification(ZModifyEvent event) throws ServiceException {
    	if (event instanceof ZModifyTagEvent) {
    		ZModifyTagEvent tevent = (ZModifyTagEvent) event;
    		mColor = tevent.getColor(mColor);
    		mName = tevent.getName(mName);
    		mUnreadCount = tevent.getUnreadCount(mUnreadCount);
    	}
    }

    public String getId() {
        return mId;
    }

    public ZMailbox getMailbox() {
        return mMailbox;
    }

    /** Returns the folder's name.  Note that this is the folder's
     *  name (e.g. <code>"foo"</code>), not its absolute pathname
     *  (e.g. <code>"/baz/bar/foo"</code>).
     * 
     * 
     */
    public String getName() {
        return mName;
    }

    /**
     * @return number of unread items in folder
     */
    public int getUnreadCount() {
        return mUnreadCount;
    }

    public Color getColor() {
        return mColor;
    }

    public ZJSONObject toZJSONObject() throws JSONException {
        ZJSONObject zjo = new ZJSONObject();
        zjo.put("id", mId);
        zjo.put("name", mName);
        zjo.put("color", mColor.name());
        zjo.put("unreadCount", mUnreadCount);
        return zjo;
    }

    public String toString() {
        return String.format("[ZTag %s]", mName);
    }

    public String dump() {
        return ZJSONObject.toString(this);
    }

    public int compareTo(Object o) {
        if (!(o instanceof ZTag)) return 0;
        ZTag other = (ZTag) o;
        return getName().compareToIgnoreCase(other.getName());
    }

    public void delete() throws ServiceException { mMailbox.deleteTag(mId); }

    public void deleteItem() throws ServiceException { delete(); }

    public void markRead() throws ServiceException { mMailbox.markTagRead(mId); }

    public void modifyColor(ZTag.Color color) throws ServiceException { mMailbox.modifyTagColor(mId, color); }

    public void rename(String newName) throws ServiceException { mMailbox.renameTag(mId, newName); }

}
