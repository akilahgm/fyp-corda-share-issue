package net.corda.fyp.flows;

import co.paralleluniverse.fibers.Suspendable;
import com.r3.corda.lib.accounts.contracts.states.AccountInfo;
import com.r3.corda.lib.accounts.workflows.UtilitiesKt;
import com.r3.corda.lib.tokens.contracts.states.FungibleToken;
import net.corda.core.contracts.StateAndRef;
import net.corda.core.flows.FlowException;
import net.corda.core.flows.FlowLogic;
import net.corda.core.flows.StartableByRPC;
import net.corda.core.node.services.vault.QueryCriteria;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
/**
 * This will be run by the BCCI node and it will issue a nonfungible token represnting each ticket to the dealer account.
 * Buyers can then buy tickets from the dealer account.
 */
@StartableByRPC
public class QuerybyAccount extends FlowLogic<String> {
    private final String whoAmI;
    public QuerybyAccount(String whoAmI) {
        this.whoAmI = whoAmI;
    }
    @Override
    @Suspendable
    public String call() throws FlowException {
        AccountInfo myAccount = UtilitiesKt.getAccountService(this).accountInfo(whoAmI).get(0).getState().getData();
        UUID id = myAccount.getIdentifier().getId();
        QueryCriteria.VaultQueryCriteria criteria = new QueryCriteria.VaultQueryCriteria().withExternalIds(Arrays.asList(id));


        //Assets
        AtomicInteger finalValue = new AtomicInteger();
        List<StateAndRef<FungibleToken>> asset = getServiceHub().getVaultService().queryBy(FungibleToken.class,criteria).getStates();
        List<String> myMoney = asset.stream().map(it -> {
            String money = "";
            int value = (int) it.getState().getData().getAmount().getQuantity();
            finalValue.set(finalValue.get() + value);
            return money;
        }).collect(Collectors.toList());
        getLogger().info("Final balance");
        getLogger().info(finalValue.toString());


        return  finalValue.toString();
    }
}
