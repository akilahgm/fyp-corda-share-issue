package net.corda.fyp.flows;

import co.paralleluniverse.fibers.Suspendable;
import com.r3.corda.lib.tokens.workflows.flows.rpc.CreateEvolvableTokens;
import net.corda.core.contracts.TransactionState;
import net.corda.core.contracts.UniqueIdentifier;
import net.corda.core.flows.FlowException;
import net.corda.core.flows.FlowLogic;
import net.corda.core.flows.StartableByRPC;
import net.corda.core.identity.Party;
import net.corda.fyp.states.ExchangeDetailState;


@StartableByRPC
public class ExchangeShares extends FlowLogic<Boolean> {

    private final String senderAccount;
    private final String correspondingId;
    private final String status;
    private final Long amount;
    private final String symbol;

    public ExchangeShares(String senderAccount, Long amount, String correspondingId,String status, String symbol) {
        this.amount = amount;
        this.senderAccount = senderAccount;
        this.correspondingId = correspondingId;
        this.status = status;
        this.symbol = symbol;
    }

    @Override
    @Suspendable
    public Boolean call() throws FlowException {

            String status = subFlow( new HttpCallFlow(correspondingId));
            System.out.println("Exchange status: "+status);
            if(!status.equals("0")){
                System.out.println("Status is not zero");
                return false;
            }
            ExchangeDetailState exchangeData= subFlow(new GetExchangeData(correspondingId));

            if(exchangeData != null){
                System.out.println("Corresponding id already recorded");
                subFlow(new ClaimHttpFlow(correspondingId));
                System.out.println("Claim requested from escrow, correspondingId - "+correspondingId);
                return true;
            }
            System.out.println("Get notary from index 0");
            final Party notary = getServiceHub().getNetworkMapCache().getNotaryIdentities().get(0); // METHOD 1
            System.out.println("Successfully get notary");
            String trxId = subFlow(new IssueCashFlow(senderAccount, amount, symbol));

            //create token type
            ExchangeDetailState evolvableTokenType = new ExchangeDetailState(getOurIdentity(), new UniqueIdentifier(), 0, correspondingId, status, senderAccount, amount, trxId);

            //wrap it with transaction state specifying the notary
            TransactionState<ExchangeDetailState> transactionState = new TransactionState<>(evolvableTokenType, notary);

            //call built in sub flow CreateEvolvableTokens. This can be called via rpc or in unit testing
            subFlow(new CreateEvolvableTokens(transactionState));
            System.out.println("Exchange status created");
            subFlow(new ClaimHttpFlow(correspondingId));
            System.out.println("Claim requested from escrow, correspondingId - "+correspondingId);
            return true;

    }
}
