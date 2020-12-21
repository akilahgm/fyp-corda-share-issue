package net.corda.fyp.states;

import com.google.common.collect.ImmutableList;
import com.r3.corda.lib.tokens.contracts.states.EvolvableTokenType;
import net.corda.core.contracts.BelongsToContract;
import net.corda.core.contracts.UniqueIdentifier;
import net.corda.core.identity.Party;
import net.corda.fyp.contracts.ExchangeDetailContract;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@BelongsToContract(ExchangeDetailContract.class)
public class ExchangeDetailState extends EvolvableTokenType {

    private final Party maintainer;
    private final UniqueIdentifier exchangeId;
    private final int fractionDigits;
    private final String correspondingId;
    private final String status;
    private final String senderAccount;
    private final Long amount;
    private final String trxId;

    public ExchangeDetailState(Party maintainer,UniqueIdentifier uniqueIdentifier, int fractionDigits, String correspondingId, String status, String senderAccount, Long amount,String trxId) {

        this.maintainer = maintainer;
        this.exchangeId = uniqueIdentifier;
        this.fractionDigits = fractionDigits;
        this.correspondingId = correspondingId;
        this.status = status;
        this.senderAccount = senderAccount;
        this.amount = amount;
        this.trxId = trxId;
    }


    public Party getMaintainer() {
        return maintainer;
    }

    @Override
    public List<Party> getMaintainers() {
        return ImmutableList.of(maintainer);
    }

    @Override
    public int getFractionDigits() {
        return this.fractionDigits;
    }

    @NotNull
    @Override
    public UniqueIdentifier getLinearId() {
        return this.exchangeId;
    }

    public UniqueIdentifier getUniqueIdentifier() {
        return exchangeId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        net.corda.fyp.states.ExchangeDetailState that = (net.corda.fyp.states.ExchangeDetailState) o;
        return getFractionDigits() == that.getFractionDigits() &&
                getMaintainer().equals(that.getMaintainer()) &&
                getCorrespondingId().equals(that.getCorrespondingId()) &&
                exchangeId.equals(that.exchangeId);
    }

    public Long getAmount() {
        return amount;
    }

    public String getSenderAccount() {
        return senderAccount;
    }

    public String getStatus() {
        return status;
    }

    public String getCorrespondingId() {
        return correspondingId;
    }
    public String getTrxId() {
        return trxId;
    }
    public UniqueIdentifier getExchangeId() {
        return exchangeId;
    }
}
