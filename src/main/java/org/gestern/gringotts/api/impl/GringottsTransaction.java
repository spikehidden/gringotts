package org.gestern.gringotts.api.impl;

import org.gestern.gringotts.Configuration;
import org.gestern.gringotts.api.Account;
import org.gestern.gringotts.api.TaxedTransaction;
import org.gestern.gringotts.api.Transaction;
import org.gestern.gringotts.api.TransactionResult;

public class GringottsTransaction implements Transaction {

    /**
     * Account from which this transaction will withdraw money.
     */
    protected final Account from;

    /**
     * Base value of this transaction.
     */
    protected final double value;

    /**
     * Create Transaction based on another (copy ctor).
     *
     * @param base transaction to copy
     */
    protected GringottsTransaction(GringottsTransaction base) {
        this.from = base.from;
        this.value = base.value;
    }

    /**
     * Create transaction with given source account and value.
     *
     * @param from  Account from which this transaction will withdraw money
     * @param value base amount of this transaction
     */
    GringottsTransaction(Account from, double value) {
        this.from = from;
        this.value = value;
    }

    @Override
    public TransactionResult to(Account to) {
        if (value < 0) {
            return TransactionResult.ERROR;
        }

        TransactionResult removed = from.remove(value);

        if (removed == TransactionResult.SUCCESS) {
            TransactionResult added = to.add(value);

            if (added != TransactionResult.SUCCESS) {
                // adding failed, refund source
                from.add(value);
            }

            // returns success or reason add failed
            return added;
        }

        // return reason remove failed
        return removed;
    }

    @Override
    public TaxedTransaction withTaxes() {
        double tax = Configuration.CONF.transactionTaxFlat + value * Configuration.CONF.transactionTaxRate;
        return new GringottsTaxedTransaction(this, tax);
    }
}
