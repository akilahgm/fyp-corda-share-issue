package net.corda.fyp.states;

import com.google.common.collect.ImmutableList;
import com.r3.corda.lib.tokens.contracts.states.EvolvableTokenType;
import net.corda.core.contracts.BelongsToContract;
import net.corda.core.contracts.UniqueIdentifier;
import net.corda.core.identity.Party;
import net.corda.fyp.contracts.TokenStateContract;
import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

@BelongsToContract(TokenStateContract.class)
public class FungibleTokenState extends EvolvableTokenType {

    private final BigDecimal valuation;
    private final Party maintainer;
    private final UniqueIdentifier uniqueIdentifier;
    private final String symbol;
    private final int fractionDigits;
    private final int quantity;

    public FungibleTokenState(BigDecimal valuation, Party maintainer,
                                   UniqueIdentifier uniqueIdentifier, int fractionDigits, String symbol, int quantity) {
        this.valuation = valuation;
        this.maintainer = maintainer;
        this.uniqueIdentifier = uniqueIdentifier;
        this.symbol = symbol;
        this.fractionDigits = fractionDigits;
        this.quantity = quantity;
    }

    public BigDecimal getValuation() {
        return valuation;
    }

    public int getTotalQuantity() {
        return quantity;
    }

    public String getSymbol() {
        return symbol;
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
        return this.uniqueIdentifier;
    }

    public UniqueIdentifier getUniqueIdentifier() {
        return uniqueIdentifier;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        net.corda.fyp.states.FungibleTokenState that = (net.corda.fyp.states.FungibleTokenState) o;
        return getFractionDigits() == that.getFractionDigits() &&
                getValuation().equals(that.getValuation()) &&
                getMaintainer().equals(that.getMaintainer()) &&
                uniqueIdentifier.equals(that.uniqueIdentifier);
    }

    @Override
    public int hashCode() {
        return Objects.hash(getValuation(), getMaintainer(), uniqueIdentifier, getFractionDigits());
    }
}

