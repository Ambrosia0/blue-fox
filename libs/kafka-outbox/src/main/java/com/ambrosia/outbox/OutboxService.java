package com.ambrosia.outbox;

/**
 * Base outbox interface
 */
public interface OutboxService {
    /**
     * Primary method for populating the outbox.
     * @param obj object to put into the outbox
     */
    void put(Object obj);
}
