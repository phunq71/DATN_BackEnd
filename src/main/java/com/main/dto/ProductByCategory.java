package com.main.dto;

import java.math.BigDecimal;
import java.util.List;

public class ProductByCategory {
    private String productID;
    private String productName;
    private String mainImageUrl;
    private BigDecimal price;
    private List<String> colors;

    public ProductByCategory() {
    }

    public ProductByCategory(String productID, String productName, String mainImageUrl, BigDecimal price, List<String> colors) {
        this.productID = productID;
        this.productName = productName;
        this.mainImageUrl = mainImageUrl;
        this.price = price;
        this.colors = colors;
    }

    public String getProductID() {
        return productID;
    }

    public void setProductID(String productID) {
        this.productID = productID;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getMainImageUrl() {
        return mainImageUrl;
    }

    public void setMainImageUrl(String mainImageUrl) {
        this.mainImageUrl = mainImageUrl;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public List<String> getColors() {
        return colors;
    }

    public void setColors(List<String> colors) {
        this.colors = colors;
    }
}
