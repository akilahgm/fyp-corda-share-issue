package net.corda.fyp.flows;

import co.paralleluniverse.fibers.Suspendable;
import net.corda.core.contracts.StateAndRef;
import net.corda.core.flows.FlowException;
import net.corda.core.flows.FlowLogic;
import net.corda.core.flows.InitiatingFlow;
import net.corda.core.flows.StartableByRPC;
import net.corda.core.utilities.ProgressTracker;
import net.corda.fyp.states.FungibleTokenState;

import java.math.BigDecimal;

@InitiatingFlow
@StartableByRPC
public class GetTokenValuation extends FlowLogic<BigDecimal> {
    private final ProgressTracker progressTracker = new ProgressTracker();
    private final String symbol;

    public GetTokenValuation(String symbol) {
        this.symbol = symbol;
    }

    @Override
    public ProgressTracker getProgressTracker() {
        return progressTracker;
    }

    @Override
    @Suspendable
    public BigDecimal call() throws FlowException {
        StateAndRef<FungibleTokenState> stateAndRef = getServiceHub().getVaultService().
                queryBy(FungibleTokenState.class).getStates().stream()
                .filter(sf -> sf.getState().getData().getSymbol().equals(symbol)).findAny()
                .orElseThrow(() -> new IllegalArgumentException("FungibleHouseTokenState symbol=\"" + symbol + "\" not found from vault"));

        BigDecimal valuation = stateAndRef.getState().getData().getValuation();
        return valuation;
    }
}
