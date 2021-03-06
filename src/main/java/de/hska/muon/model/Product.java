package de.hska.muon.model;

import com.googlecode.cqengine.attribute.SimpleAttribute;
import com.googlecode.cqengine.query.option.QueryOptions;

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

    private Integer productId;
    private String details;
    private String name;
    private Integer price;
    private Integer categoryId;


    // -- Cq-Attributes
    public static final SimpleAttribute<Product, Integer> PROCUCT_ID = new SimpleAttribute<Product, Integer>("productId") {
        public Integer getValue(Product product, QueryOptions queryOptions) { return product.getProductId(); }
    };

    public static final SimpleAttribute<Product, Integer> CATEGORY_ID = new SimpleAttribute<Product, Integer>("categoryId") {
        public Integer getValue(Product product, QueryOptions queryOptions) { return product.getCategoryId(); }
    };

    // -- Getter and setters

    public Integer getProductId() {
        return productId;
    }

    public void setProductId(final Integer productId) {
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

    public Integer getPrice() {
        return price;
    }

    public void setPrice(final Integer price) {
        this.price = price;
    }

    public Integer getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(final Integer categoryId) {
        this.categoryId = categoryId;
    }

    @Override
    public String toString() {
        return "Product{" +
                "productId='" + productId + '\'' +
                ", details='" + details + '\'' +
                ", name='" + name + '\'' +
                ", price=" + price +
                ", categoryId=" + categoryId +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Product product = (Product) o;

        if (productId != null ? !productId.equals(product.productId) : product.productId != null) return false;
        if (details != null ? !details.equals(product.details) : product.details != null) return false;
        if (name != null ? !name.equals(product.name) : product.name != null) return false;
        if (price != null ? !price.equals(product.price) : product.price != null) return false;
        return categoryId != null ? categoryId.equals(product.categoryId) : product.categoryId == null;
    }

    @Override
    public int hashCode() {
        int result = productId != null ? productId.hashCode() : 0;
        result = 31 * result + (details != null ? details.hashCode() : 0);
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (price != null ? price.hashCode() : 0);
        result = 31 * result + (categoryId != null ? categoryId.hashCode() : 0);
        return result;
    }
}
