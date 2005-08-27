/*
 * ***** BEGIN LICENSE BLOCK *****
 * Version: ZPL 1.1
 * 
 * The contents of this file are subject to the Zimbra Public License
 * Version 1.1 ("License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://www.zimbra.com/license
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See
 * the License for the specific language governing rights and limitations
 * under the License.
 * 
 * The Original Code is: Zimbra Collaboration Suite.
 * 
 * The Initial Developer of the Original Code is Zimbra, Inc.
 * Portions created by Zimbra are Copyright (C) 2005 Zimbra, Inc.
 * All Rights Reserved.
 * 
 * Contributor(s): 
 * 
 * ***** END LICENSE BLOCK *****
 */

package com.zimbra.cs.service.admin;

import java.util.Map;

import com.zimbra.cs.service.Element;
import com.zimbra.cs.service.ServiceException;
import com.zimbra.cs.store.Volume;
import com.zimbra.soap.ZimbraContext;

public class SetCurrentVolume extends AdminDocumentHandler {

    public Element handle(Element request, Map context)
            throws ServiceException {
        ZimbraContext lc = getZimbraContext(context);
        Element eVol = request.getElement(AdminService.E_CURRENT_VOLUME);
        String volTypeStr = eVol.getAttribute(AdminService.A_VOLUME_TYPE);
        int volType = Volume.parseTypeStr(volTypeStr);
        short id = (short) eVol.getAttributeLong(AdminService.A_ID);
        Volume.setCurrentVolume(id, volType);
        Element response = lc.createElement(AdminService.SET_CURRENT_VOLUME_RESPONSE);
        return response;
    }
}
