
package io.taiji.faucet.model;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Water {

    
    
    
    public enum CurrencyEnum {
        
        TAIJI ("taiji");
        

        private final String value;

        CurrencyEnum(String value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return String.valueOf(value);
        }

        public static CurrencyEnum fromValue(String text) {
            for (CurrencyEnum b : CurrencyEnum.values()) {
                if (String.valueOf(b.value).equals(text)) {
                return b;
                }
            }
            return null;
        }
    }

    private CurrencyEnum currency;

    
    
    private Integer amount;
    
    
    
    public enum UnitEnum {
        
        SHELL ("SHELL"),
        
        KSHELL ("KSHELL"),
        
        MSHELL ("MSHELL"),
        
        TAIJI ("TAIJI"),
        
        KTAIJI ("KTAIJI");
        

        private final String value;

        UnitEnum(String value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return String.valueOf(value);
        }

        public static UnitEnum fromValue(String text) {
            for (UnitEnum b : UnitEnum.values()) {
                if (String.valueOf(b.value).equals(text)) {
                return b;
                }
            }
            return null;
        }
    }

    private UnitEnum unit;

    
    

    public Water () {
    }

    
    
    @JsonProperty("currency")
    public CurrencyEnum getCurrency() {
        return currency;
    }

    public void setCurrency(CurrencyEnum currency) {
        this.currency = currency;
    }
    
    
    
    @JsonProperty("amount")
    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }
    
    
    
    @JsonProperty("unit")
    public UnitEnum getUnit() {
        return unit;
    }

    public void setUnit(UnitEnum unit) {
        this.unit = unit;
    }
    
    

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Water Water = (Water) o;

        return Objects.equals(currency, Water.currency) &&
        Objects.equals(amount, Water.amount) &&
        
        Objects.equals(unit, Water.unit);
    }

    @Override
    public int hashCode() {
        return Objects.hash(currency, amount,  unit);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class Water {\n");
        
        sb.append("    currency: ").append(toIndentedString(currency)).append("\n");
        sb.append("    amount: ").append(toIndentedString(amount)).append("\n");
        sb.append("    unit: ").append(toIndentedString(unit)).append("\n");
        sb.append("}");
        return sb.toString();
    }

    /**
     * Convert the given object to string with each line indented by 4 spaces
     * (except the first line).
     */
    private String toIndentedString(Object o) {
        if (o == null) {
            return "null";
        }
        return o.toString().replace("\n", "\n    ");
    }
}
