
package model;

import java.util.Date;

public class Promotion {

    private String promotionId;
    private String name;
    private double discountPercent;
    private Date startDate;
    private Date endDate;

    public Promotion() {
    }

    public Promotion(String promotionId, String name,
                     double discountPercent,
                     Date startDate, Date endDate) {
        this.promotionId = promotionId;
        this.name = name;
        this.discountPercent = discountPercent;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public String getPromotionId() {
        return promotionId;
    }

    public void setPromotionId(String promotionId) {
        this.promotionId = promotionId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getDiscountPercent() {
        return discountPercent;
    }

    public void setDiscountPercent(double discountPercent) {
        this.discountPercent = discountPercent;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public boolean isActive() {
        Date now = new Date();

        return startDate != null
                && endDate != null
                && !now.before(startDate)
                && !now.after(endDate);
    }

    public void applyDiscount() {
    }
}
