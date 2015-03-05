package com.zimbra.cs.index;



/**
 * Base class for describing queued indexing tasks
 * @author Greg Solovyev
 *
 */
public abstract class AbstractIndexingTasksLocator {
    protected final int mailboxID;
    protected final int mailboxSchemaGroupID;
    protected final String accountID;
    protected final boolean indexAttachments;
    
    protected AbstractIndexingTasksLocator (int mailboxID, int mailboxSchemaGroupID, String accountID) {
        this.mailboxID = mailboxID;
        this.mailboxSchemaGroupID = mailboxSchemaGroupID;
        this.accountID = accountID;
        this.indexAttachments = false;
    }
    
    protected AbstractIndexingTasksLocator (int mailboxID, int mailboxSchemaGroupID, String accountID,  boolean indexAttachments) {
        this.mailboxID = mailboxID;
        this.mailboxSchemaGroupID = mailboxSchemaGroupID;
        this.accountID = accountID;
        this.indexAttachments = indexAttachments;
    }
    
    public int getMailboxID() {
        return mailboxID;
    }

    public String getAccountID() {
        return accountID;
    }

    public int getMailboxSchemaGroupID() {
        return mailboxSchemaGroupID;
    }

    public boolean attachmentIndexingEnabled() {
        return indexAttachments;
    }
}
