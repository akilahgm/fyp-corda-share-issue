package net.corda.fyp.flows;

import co.paralleluniverse.fibers.Suspendable;
import com.r3.corda.lib.tokens.workflows.flows.evolvable.UpdateEvolvableTokenFlow;
import com.r3.corda.lib.tokens.workflows.flows.evolvable.UpdateEvolvableTokenFlowHandler;
import kotlin.Unit;
import net.corda.core.contracts.StateAndRef;
import net.corda.core.flows.*;
import net.corda.core.identity.Party;
import net.corda.core.transactions.SignedTransaction;
import net.corda.fyp.states.ExchangeDetailState;

import java.util.ArrayList;
import java.util.List;

import static java.util.Collections.emptyList;

public class UpdateExchange {
    @StartableByRPC
    @InitiatingFlow
    public static class UpdateExchangeInitiator extends FlowLogic<String> {


        private final String status;
        private final String correspondingId;

        public UpdateExchangeInitiator(String correspondingId, String status) {
            this.correspondingId = correspondingId;
            this.status = status;
        }

        @Override
        @Suspendable
        public String call() throws FlowException {
            final Party notary = getServiceHub().getNetworkMapCache().getNotaryIdentities().get(0); // METHOD 1
            // final Party notary = getServiceHub().getNetworkMapCache().getNotary(CordaX500Name.parse("O=Notary,L=London,C=GB")); // METHOD 2

            StateAndRef<ExchangeDetailState> exchangeState = null;
            for (StateAndRef<ExchangeDetailState> sf : getServiceHub().getVaultService().
                    queryBy(ExchangeDetailState.class).getStates()) {
                if (sf.getState().getData().getCorrespondingId().equals(correspondingId)) {
                    exchangeState =sf;
                }
            }
            ExchangeDetailState data = exchangeState.getState().getData();

            ExchangeDetailState evolvableToken = new ExchangeDetailState(getOurIdentity(),data.getLinearId(),0,correspondingId,status,data.getSenderAccount(),data.getAmount(),data.getTrxId());


            List<FlowSession> partySessions = new ArrayList<>();
            partySessions.add(initiateFlow(notary));
            //Issue fungible tokens to specified account
            SignedTransaction stx = subFlow(new UpdateEvolvableTokenFlow(exchangeState,
                    evolvableToken,
                    emptyList(),
                    partySessions));

            return "Update token";
        }
    }

    @InitiatedBy(UpdateExchange.UpdateExchangeInitiator.class)
    public static class UpdateExchangeResponder extends FlowLogic<Unit> {
        private FlowSession counterSession;

        public UpdateExchangeResponder(FlowSession counterSession) {
            this.counterSession = counterSession;
        }

        @Suspendable
        @Override
        public Unit call() throws FlowException {
            // To implement the responder flow, simply call the subflow of UpdateEvolvableTokenFlowHandler
            return subFlow(new UpdateEvolvableTokenFlowHandler(counterSession));
        }
    }
}
