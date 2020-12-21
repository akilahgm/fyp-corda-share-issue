package net.corda.fypn.flows;

import co.paralleluniverse.fibers.Suspendable;
import com.google.common.collect.ImmutableList;
import com.r3.corda.lib.tokens.contracts.types.TokenType;
import com.r3.corda.lib.tokens.workflows.flows.evolvable.UpdateEvolvableTokenFlow;
import com.r3.corda.lib.tokens.workflows.flows.evolvable.UpdateEvolvableTokenFlowHandler;
import com.r3.corda.lib.tokens.workflows.flows.rpc.UpdateEvolvableToken;
import kotlin.Unit;
import net.corda.core.contracts.StateAndRef;
import net.corda.core.contracts.TransactionState;
import net.corda.core.contracts.UniqueIdentifier;
import net.corda.core.flows.*;
import net.corda.core.identity.CordaX500Name;
import net.corda.core.identity.Party;
import net.corda.core.messaging.CordaRPCOps;
import net.corda.core.node.NodeInfo;
import net.corda.core.node.services.IdentityService;
import net.corda.core.transactions.SignedTransaction;
import net.corda.fyp.states.FungibleTokenState;

import java.math.BigDecimal;
import java.util.*;

import static java.util.Collections.emptyList;

/**
 * flow start UpdateToken$UpdateTokenInitiator amount : 100 , symbol : house , valuation : 5
 * This should be run by the bank node. The Bank node issues Cash (Fungible Tokens) to the buyers who want to buy he Ipl ticket.
 */
public class UpdateToken {
    @StartableByRPC
    @InitiatingFlow
    public static class UpdateTokenInitiator extends FlowLogic<String> {


        private final BigDecimal valuation;
        private String symbol;
        private int quantity;

        public UpdateTokenInitiator(String symbol, BigDecimal valuation,int quantity) {
            this.symbol = symbol;
            this.valuation = valuation;
            this.quantity = quantity;
        }

        @Override
        @Suspendable
        public String call() throws FlowException {

            //get house states on ledger with uuid as input tokenId
            StateAndRef<FungibleTokenState> stateAndRef = getServiceHub().getVaultService().
                    queryBy(FungibleTokenState.class).getStates().stream()
                    .filter(sf -> sf.getState().getData().getSymbol().equals(symbol)).findAny()
                    .orElseThrow(() -> new IllegalArgumentException("FungibleHouseTokenState symbol=\"" + symbol + "\" not found from vault"));

            final Party notary = getServiceHub().getNetworkMapCache().getNotaryIdentities().get(0);
            //create token type
            FungibleTokenState newEvolvableTokenType = new FungibleTokenState(valuation, getOurIdentity(),
                    stateAndRef.getState().getData().getLinearId(), 0, this.symbol,quantity);

            List<FlowSession> partySessions = new ArrayList<>();
            partySessions.add(initiateFlow(notary));
            //Issue fungible tokens to specified account
            SignedTransaction stx = subFlow(new UpdateEvolvableTokenFlow(stateAndRef,
                    newEvolvableTokenType,
                    emptyList(),
                    partySessions));

            return "Update token";
        }

        public TokenType getInstance(String currencyCode) {
            Currency currency = Currency.getInstance(currencyCode);
            return new TokenType(currency.getCurrencyCode(), 0);
        }

        public List<Party> getObserverLegalIdenties(IdentityService identityService) {
            List<Party> observers = new ArrayList<>();
            for (String observerName : getObserversNames()) {
                Set<Party> observerSet = identityService.partiesFromName(observerName, false);
                if (observerSet.size() != 1) {
                    final String errMsg = String.format("Found %d identities for the observer.", observerSet.size());
                    throw new IllegalStateException(errMsg);
                }
                observers.add(observerSet.iterator().next());
            }
            return observers;
        }

        public List<String> getObserversNames() {
            return ImmutableList.of("Observer");
        }
    }

    @InitiatedBy(UpdateToken.UpdateTokenInitiator.class)
    public static class UpdateTokenResponder extends FlowLogic<Unit> {
        private FlowSession counterSession;

        public UpdateTokenResponder(FlowSession counterSession) {
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
