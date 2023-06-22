package com.vkontakte.miracle.model.longpoll.messages.fields;

public class MessageFlags {

    private static final int FLAG_UNREAD = 0x1;
    private static final int FLAG_OUTBOX = 0x2;
    private static final int FLAG_DELETED = 0x128;
    private static final int FLAG_DELETE_FOR_ALL = 0x131072;
    private static final int FLAG_NOT_DELIVERED = 262144;

    private final boolean unread;
    private final boolean outbox;
    private final boolean deleted;
    private final boolean deletedForAll;
    private final boolean notDelivered;

    public MessageFlags(int flags) {
        this.unread = (flags & FLAG_UNREAD) != 0;
        this.outbox = (flags & FLAG_OUTBOX) != 0;
        this.deleted = (flags & FLAG_DELETED) != 0;
        this.deletedForAll = (flags & FLAG_DELETE_FOR_ALL) != 0;
        this.notDelivered = (flags & FLAG_NOT_DELIVERED) != 0;
    }

    public boolean isUnread() {
        return unread;
    }

    public boolean isOutbox() {
        return outbox;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public boolean isDeletedForAll() {
        return deletedForAll;
    }

    public boolean isNotDelivered() {
        return notDelivered;
    }
}
