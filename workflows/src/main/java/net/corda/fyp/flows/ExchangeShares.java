package net.corda.fyp.flows;

import co.paralleluniverse.fibers.Suspendable;
import com.r3.corda.lib.tokens.workflows.flows.rpc.CreateEvolvableTokens;
import net.corda.core.contracts.TransactionState;
import net.corda.core.contracts.UniqueIdentifier;
import net.corda.core.flows.FlowException;
import net.corda.core.flows.FlowLogic;
import net.corda.core.flows.StartableByRPC;
import net.corda.core.identity.Party;
import net.corda.core.transactions.SignedTransaction;
import net.corda.fyp.states.ExchangeDetailState;

import java.math.BigDecimal;


@StartableByRPC
public class ExchangeShares extends FlowLogic<SignedTransaction> {

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
    public SignedTransaction call() throws FlowException {
        // Obtain a reference to a notary we wish to use.
        /** METHOD 1: Take first notary on network, WARNING: use for test, non-prod environments, and single-notary networks only!*
         *  METHOD 2: Explicit selection of notary by CordaX500Name - argument can by coded in flow or parsed from config (Preferred)
         *
         *  * - For production you always want to use Method 2 as it guarantees the expected notary is returned.
         */
        final Party notary = getServiceHub().getNetworkMapCache().getNotaryIdentities().get(0); // METHOD 1
        // final Party notary = getServiceHub().getNetworkMapCache().getNotary(CordaX500Name.parse("O=Notary,L=London,C=GB")); // METHOD 2

        String trxId = subFlow(new IssueCashFlow(senderAccount,amount,symbol));

        //create token type
        ExchangeDetailState evolvableTokenType = new ExchangeDetailState(getOurIdentity(),new UniqueIdentifier(),0,correspondingId,status,senderAccount,amount,trxId);

        //wrap it with transaction state specifying the notary
        TransactionState<ExchangeDetailState> transactionState = new TransactionState<>(evolvableTokenType, notary);

        //call built in sub flow CreateEvolvableTokens. This can be called via rpc or in unit testing
        return subFlow(new CreateEvolvableTokens(transactionState));
    }
}