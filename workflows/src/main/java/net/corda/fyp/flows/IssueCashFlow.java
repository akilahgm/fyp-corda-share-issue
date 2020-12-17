package net.corda.fyp.flows;

import co.paralleluniverse.fibers.Suspendable;
import com.r3.corda.lib.accounts.contracts.states.AccountInfo;
import com.r3.corda.lib.accounts.workflows.UtilitiesKt;
import com.r3.corda.lib.accounts.workflows.flows.RequestKeyForAccount;
import com.r3.corda.lib.tokens.contracts.states.FungibleToken;
import com.r3.corda.lib.tokens.contracts.types.IssuedTokenType;
import com.r3.corda.lib.tokens.contracts.types.TokenPointer;
import com.r3.corda.lib.tokens.contracts.types.TokenType;
import com.r3.corda.lib.tokens.workflows.flows.rpc.IssueTokens;
import com.r3.corda.lib.tokens.workflows.utilities.FungibleTokenBuilder;
import net.corda.core.contracts.Amount;
import net.corda.core.contracts.InsufficientBalanceException;
import net.corda.core.contracts.StateAndRef;
import net.corda.core.flows.*;
import net.corda.core.identity.AnonymousParty;
import net.corda.core.transactions.SignedTransaction;
import net.corda.fyp.states.FungibleTokenState;

import java.util.Arrays;
import java.util.Currency;

/**
 * This should be run by the bank node. The Bank node issues Cash (Fungible Tokens) to the buyers who want to buy he Ipl ticket.
 */

@StartableByRPC
@InitiatingFlow
public class IssueCashFlow extends FlowLogic<String> {

    private final String accountName;
    private final Long amount;
    private String symbol;

    public IssueCashFlow(String accountName, Long amount, String symbol) {
        this.accountName = accountName;
        this.amount = amount;
        this.symbol = symbol;
    }

    @Override
    @Suspendable
    public String call() throws FlowException {
        String availableTokens = subFlow(new GetAvailableTokens(symbol));
        int availableTokensInt =Integer.parseInt(availableTokens);
        if(availableTokensInt<amount){
            throw new FlowException("Expected token amount exceed maximum available tokens");
        }
        getLogger().info("available tokens");
        getLogger().info(availableTokens);
        //get house states on ledger with uuid as input tokenId
        StateAndRef<FungibleTokenState> stateAndRef = getServiceHub().getVaultService().
                queryBy(FungibleTokenState.class).getStates().stream()
                .filter(sf->sf.getState().getData().getSymbol().equals(symbol)).findAny()
                .orElseThrow(()-> new IllegalArgumentException("FungibleTokenState symbol=\""+symbol+"\" not found from vault"));
        //get the RealEstateEvolvableTokenType object
        FungibleTokenState evolvableTokenType = stateAndRef.getState().getData();
        //get the pointer pointer to the T20CricketTicket
        TokenPointer tokenPointer = evolvableTokenType.toPointer(evolvableTokenType.getClass());
        //assign the issuer to the T20CricketTicket type who is the BCCI node
        IssuedTokenType issuedTokenType = new IssuedTokenType(getOurIdentity(), tokenPointer);

        //Dealer node has already shared accountinfo with the bank when we ran the CreateAndShareAccountFlow. So this bank node will
        //have access to AccountInfo of the buyer. Retrieve it using the AccountService. AccountService has certain helper methods, take a look at them.
        AccountInfo accountInfo = UtilitiesKt.getAccountService(this).accountInfo(accountName).get(0).getState().getData();

        //To transact with any account, we have to request for a Key from the node hosting the account. For this we use RequestKeyForAccount inbuilt flow.
        //This will return a Public key wrapped in an AnonymousParty class.
        AnonymousParty anonymousParty = subFlow(new RequestKeyForAccount(accountInfo));

        //Create a fungible token for issuing cash to account
        FungibleToken fungibleToken = new FungibleToken(new Amount(this.amount, issuedTokenType), anonymousParty, null);

        //Issue fungible tokens to specified account
        SignedTransaction stx =subFlow(new IssueTokens(Arrays.asList(fungibleToken)));

        return "Issued "+amount+" token(s) to "+accountName + " Available tokens " + (availableTokensInt - amount)+
                "\ntxId: "+stx.getId().toString()+"";
    }

    public TokenType getInstance(String currencyCode) {
        Currency currency = Currency.getInstance(currencyCode);
        return new TokenType(currency.getCurrencyCode(), 0);
    }
}
