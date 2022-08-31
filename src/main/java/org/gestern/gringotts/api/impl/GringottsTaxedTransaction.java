package org.gestern.gringotts.api.impl;

import org.gestern.gringotts.api.Account;
import org.gestern.gringotts.api.TaxedTransaction;
import org.gestern.gringotts.api.TransactionResult;

/**
 * The type Gringotts taxed transaction.
 */
public class GringottsTaxedTransaction extends GringottsTransaction implements TaxedTransaction {
    /**
     * Taxes to apply to transaction.
     */
    private final double taxes;
    /**
     * Taxes will be added to this account vault if any
     */
    private Account collector;

    /**
     * Create taxed transaction, adding given amount of taxes to the given base transaction
     *
     * @param base  transaction on which the tax is based
     * @param taxes taxes to apply to transaction
     */
    protected GringottsTaxedTransaction(GringottsTransaction base, double taxes) {
        super(base);

        this.taxes = taxes;
    }

    /**
     * Complete the transaction by sending the transaction amount to a given account.
     *
     * @param recipient Account to which receives the value of this transaction.
     * @return result of the transaction.
     */
    @Override
    public TransactionResult to(Account recipient) {
        TransactionResult taxResult = from.remove(taxes);

        if (taxResult != TransactionResult.SUCCESS) {
            return taxResult;
        }

        TransactionResult result = super.to(recipient);

        // undo taxing if transaction failed
        if (result != TransactionResult.SUCCESS) {
            from.add(taxes);
        } else {
            if (collector != null) {
                collector.add(taxes);
            }
        }

        return result;
    }

    /**
     * Add a tax collector to this taxed transaction. The tax collector account receives the taxes from this
     * transaction.
     *
     * @param taxCollector account to receive the taxes.
     * @return taxed transaction with tax collector
     */
    @Override
    public TaxedTransaction setCollectedBy(Account taxCollector) {
        if (this.collector != null) {
            throw new RuntimeException("Collector is already set");
        }

        this.collector = taxCollector;

        return this;
    }

    /**
     * Return the amount of taxes to be paid in this transaction.
     *
     * @return the amount of taxes to be paid in this transaction.
     */
    @Override
    public double getTax() {
        return taxes;
    }
}
