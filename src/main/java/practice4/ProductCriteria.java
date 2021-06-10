package practice4;

import java.util.Objects;

public class ProductCriteria {

    private String name;
    private Double priceFrom;
    private Double priceTill;
    private Double amountFrom;
    private Double amountTill;


    public ProductCriteria(String name, Double priceFrom, Double priceTill, Double amountFrom, Double amountTill) {
        this.name = name;
        this.priceFrom = priceFrom;
        this.priceTill = priceTill;
        this.amountFrom = amountFrom;
        this.amountTill = amountTill;
    }

    public ProductCriteria(){

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getPriceFrom() {
        return priceFrom;
    }

    public void setPriceFrom(Double priceFrom) {
        this.priceFrom = priceFrom;
    }

    public Double getPriceTill() {
        return priceTill;
    }

    public void setPriceTill(Double priceTill) {
        this.priceTill = priceTill;
    }

    public Double getAmountFrom() {
        return amountFrom;
    }

    public void setAmountFrom(Double amountFrom) {
        this.amountFrom = amountFrom;
    }

    public Double getAmountTill() {
        return amountTill;
    }

    public void setAmountTill(Double amountTill) {
        this.amountTill = amountTill;
    }

    @Override
    public String toString() {
        return "ProductCriteria{" +
                "name='" + name + '\'' +
                ", priceFrom=" + priceFrom +
                ", priceTill=" + priceTill +
                ", amountFrom=" + amountFrom +
                ", amountTill=" + amountTill +
                '}';
    }
}
