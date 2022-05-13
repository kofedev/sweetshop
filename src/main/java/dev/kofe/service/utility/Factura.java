package dev.kofe.service.utility;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Factura {

    private final BigDecimal totalNettoOfOrderWithoutDelivery;
    private final BigDecimal costNettoOfDelivery;
    private final BigDecimal valueOfVATinPercents;

    public Factura(
            BigDecimal totalNettoOfOrderWithoutDelivery,
            BigDecimal costNettoOfDelivery,
            String valueOfVATinPercentsString) {
        this.totalNettoOfOrderWithoutDelivery = totalNettoOfOrderWithoutDelivery;
        this.costNettoOfDelivery = costNettoOfDelivery;
        this.valueOfVATinPercents = new BigDecimal(valueOfVATinPercentsString);
    }

    public Factura(BigDecimal totalNettoOfOrderWithoutDelivery, String valueOfVATinPercentsString) {
        this.totalNettoOfOrderWithoutDelivery = totalNettoOfOrderWithoutDelivery;
        this.valueOfVATinPercents = new BigDecimal(valueOfVATinPercentsString);
        this.costNettoOfDelivery = new BigDecimal("0.0");
    }

    public BigDecimal getTotalNettoOfOrderPlusDelivery () {
        BigDecimal result = totalNettoOfOrderWithoutDelivery.add(costNettoOfDelivery);
        result = result.setScale(2, RoundingMode.HALF_EVEN);
        return result;
    }

    public BigDecimal getPercentVATCalculatedFromTotalNettoOfOrderPlusDelivery () {
        BigDecimal result =
                getTotalNettoOfOrderPlusDelivery().multiply(valueOfVATinPercents.multiply(new BigDecimal("0.01")));
        result = result.setScale(2, RoundingMode.HALF_EVEN);
        return result;
    }

    public BigDecimal getPercentVATCalculatedFromTotalNettoOfOrder () {
        BigDecimal result =
                totalNettoOfOrderWithoutDelivery.multiply(valueOfVATinPercents.multiply(new BigDecimal("0.01")));
        result = result.setScale(2, RoundingMode.HALF_EVEN);
        return result;
    }

    public BigDecimal getTotalBrutto () {
        BigDecimal result =
                getTotalNettoOfOrderPlusDelivery().add(getPercentVATCalculatedFromTotalNettoOfOrderPlusDelivery());

        result = result.setScale(2, RoundingMode.HALF_EVEN);
        return result;
    }

    public BigDecimal getValueOfVATinPercents () {
        return valueOfVATinPercents;
    }

    public String getFacturaString(long orderId) {
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        Date date = new Date();

        return formatter.format(date) + "-P" + String.valueOf (orderId);
    }

}
