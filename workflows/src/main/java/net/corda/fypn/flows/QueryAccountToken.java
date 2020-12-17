package net.corda.fypn.flows;

import co.paralleluniverse.fibers.Suspendable;
import com.r3.corda.lib.tokens.contracts.types.TokenPointer;
import com.r3.corda.lib.tokens.contracts.types.TokenType;
import com.r3.corda.lib.tokens.workflows.utilities.QueryUtilities;
import net.corda.core.contracts.TransactionState;
import net.corda.fyp.states.FungibleTokenState;
import com.r3.corda.lib.tokens.contracts.states.FungibleToken;
import net.corda.core.contracts.Amount;
import net.corda.core.contracts.StateAndRef;
import net.corda.core.flows.FlowException;
import net.corda.core.flows.FlowLogic;
import net.corda.core.flows.InitiatingFlow;
import net.corda.core.flows.StartableByRPC;
import net.corda.core.utilities.ProgressTracker;

import java.util.Set;
import java.util.stream.Collectors;

public class QueryAccountToken {

    @InitiatingFlow
    @StartableByRPC
    public static class GetAccountTokenBalance extends FlowLogic<String> {
        private final ProgressTracker progressTracker = new ProgressTracker();
        private final String symbol;


        public GetAccountTokenBalance(String symbol) {
            this.symbol = symbol;
        }

        @Override
        public ProgressTracker getProgressTracker() {
            return progressTracker;
        }

        @Override
        @Suspendable
        public String call() throws FlowException {
            //get a set of the RealEstateEvolvableTokenType object on ledger with uuid as input tokenId
            Set<FungibleTokenState> evolvableTokenTypeSet = getServiceHub().getVaultService().
                    queryBy(FungibleTokenState.class).getStates().stream()
                    .filter(sf->sf.getState().getData().getSymbol().equals(symbol)).map(StateAndRef::getState)
                    .map(TransactionState::getData).collect(Collectors.toSet());
            if (evolvableTokenTypeSet.isEmpty()){
                throw new IllegalArgumentException("FungibleHouseTokenState symbol=\""+symbol+"\" not found from vault");
            }

            // Save the result
            String result="";

            for (FungibleTokenState evolvableTokenType : evolvableTokenTypeSet){
                //get the pointer pointer to the house
                TokenPointer<FungibleTokenState> tokenPointer = evolvableTokenType.toPointer(FungibleTokenState.class);
                //query balance or each different Token
                Amount<TokenType> amount = QueryUtilities.tokenBalance(getServiceHub().getVaultService(), tokenPointer);
                result += "\n You currently have "+ amount.getQuantity()+ " " + symbol + " Tokens issued by "+evolvableTokenType.getMaintainer()+"\n";

            }
            return result;
        }
    }

}
