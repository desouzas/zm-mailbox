/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2008, 2009, 2010, 2011 Zimbra Software, LLC.
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
package com.zimbra.cs.im;

import com.zimbra.common.soap.Element;
import com.zimbra.common.soap.IMConstants;

public class IMChatCloseNotification extends IMNotification {
    String mThreadId;
    
    IMChatCloseNotification(String threadId) {
        mThreadId = threadId;
    }
    
    public Element toXml(Element parent) {
        Element elt = create(parent, "chatclosed");
        elt.addAttribute(IMConstants.A_THREAD_ID, mThreadId);
        return elt;
    }

}
