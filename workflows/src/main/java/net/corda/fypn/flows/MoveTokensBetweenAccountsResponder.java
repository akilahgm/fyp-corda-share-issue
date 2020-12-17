package net.corda.fypn.flows;

import co.paralleluniverse.fibers.Suspendable;
import net.corda.core.flows.*;

@InitiatedBy(MoveTokensBetweenAccounts.class)
class MoveTokensBetweenAccountsResponder extends FlowLogic<Void> {

    private final FlowSession otherSide;

    public MoveTokensBetweenAccountsResponder(FlowSession otherSide) {
        this.otherSide = otherSide;
    }

    @Override
    @Suspendable
    public Void call() throws FlowException {

        subFlow(new ReceiveFinalityFlow(otherSide));

        return null;
    }
}
