package net.corda.fyp.contracts;

import com.r3.corda.lib.tokens.contracts.EvolvableTokenContract;
import net.corda.core.contracts.Contract;
import net.corda.core.transactions.LedgerTransaction;

public class ExchangeDetailContract extends EvolvableTokenContract implements Contract {

    @Override
    public void additionalCreateChecks(LedgerTransaction tx) {
        // add additional create checks here
    }

    @Override
    public void additionalUpdateChecks(LedgerTransaction tx) {
        // add additional update checks here
    }
}