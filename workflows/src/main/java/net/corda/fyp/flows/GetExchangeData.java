package net.corda.fyp.flows;

import co.paralleluniverse.fibers.Suspendable;
import net.corda.core.contracts.StateAndRef;
import net.corda.core.contracts.TransactionState;
import net.corda.core.flows.FlowException;
import net.corda.core.flows.FlowLogic;
import net.corda.core.flows.StartableByRPC;
import net.corda.core.identity.Party;
import net.corda.core.transactions.SignedTransaction;
import net.corda.fyp.states.ExchangeDetailState;

import java.util.HashSet;
import java.util.Set;

@StartableByRPC
public class GetExchangeData extends FlowLogic<ExchangeDetailState> {

    private final String correspondingId;

    public GetExchangeData( String correspondingId) {
        this.correspondingId = correspondingId;
    }

    @Override
    @Suspendable
    public ExchangeDetailState call() throws FlowException {

        final Party notary = getServiceHub().getNetworkMapCache().getNotaryIdentities().get(0); // METHOD 1
        // final Party notary = getServiceHub().getNetworkMapCache().getNotary(CordaX500Name.parse("O=Notary,L=London,C=GB")); // METHOD 2

        ExchangeDetailState exchangeState = null;
        for (StateAndRef<ExchangeDetailState> sf : getServiceHub().getVaultService().
                queryBy(ExchangeDetailState.class).getStates()) {
            if (sf.getState().getData().getCorrespondingId().equals(correspondingId)) {
                TransactionState<ExchangeDetailState> state = sf.getState();
                ExchangeDetailState data = state.getData();
                exchangeState =data;
            }
        }
        if(exchangeState == null){
            return null;
        }
        getLogger().info("evaluable token");
        getLogger().info(exchangeState.getCorrespondingId());
        getLogger().info(exchangeState.getUniqueIdentifier().toString());
        getLogger().info(exchangeState.getTrxId());
        return exchangeState;
    }
}