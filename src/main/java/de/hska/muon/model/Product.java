package de.hska.muon.model;

/**
 * Represents a product.
 *
 * Created by amish on 5/4/17.
 */
public class Product {

    /**
     * Creates a new instance of product.
     */
    public Product() {

    }

    private String productId;
    private String details;
    private String name;
    private double price;

    private String categoryName;
    private int categoryId;

    public String getProductId() {
        return productId;
    }

    public void setProductId(final String productId) {
        this.productId = productId;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(final String details) {
        this.details = details;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(final double price) {
        this.price = price;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(final int categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(final String categoryName) {
        this.categoryName = categoryName;
    }


    @Override
    public String toString() {
        return "Product{" +
                "productId='" + productId + '\'' +
                ", details='" + details + '\'' +
                ", name='" + name + '\'' +
                ", price=" + price +
                ", categoryName='" + categoryName + '\'' +
                ", categoryId=" + categoryId +
                '}';
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final Product product = (Product) o;

        if (Double.compare(product.price, price) != 0) return false;
        if (categoryId != product.categoryId) return false;
        if (productId != null ? !productId.equals(product.productId) : product.productId != null) return false;
        if (details != null ? !details.equals(product.details) : product.details != null) return false;
        if (name != null ? !name.equals(product.name) : product.name != null) return false;
        return categoryName != null ? categoryName.equals(product.categoryName) : product.categoryName == null;
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = productId != null ? productId.hashCode() : 0;
        result = 31 * result + (details != null ? details.hashCode() : 0);
        result = 31 * result + (name != null ? name.hashCode() : 0);
        temp = Double.doubleToLongBits(price);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        result = 31 * result + (categoryName != null ? categoryName.hashCode() : 0);
        result = 31 * result + categoryId;
        return result;
    }

}
