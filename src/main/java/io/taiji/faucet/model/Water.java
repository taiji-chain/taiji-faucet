
package io.taiji.faucet.model;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Water {
    Integer amount;
    String currency;
    String unit;

    public Water () {
    }

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Water water = (Water) o;
        return Objects.equals(amount, water.amount) &&
                Objects.equals(currency, water.currency) &&
                Objects.equals(unit, water.unit);
    }

    @Override
    public int hashCode() {

        return Objects.hash(amount, currency, unit);
    }

    @Override
    public String toString() {
        return "Water{" +
                "amount=" + amount +
                ", currency='" + currency + '\'' +
                ", unit='" + unit + '\'' +
                '}';
    }
}
