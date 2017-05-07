package de.hska.muon.client;

import de.hska.muon.model.Category;
import de.hska.muon.model.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static de.hska.muon.client.Clients.createHeaderWithUserId;

/**
 * This class is taking care of redirecting all the calls to the product-service.
 *
 * Created by amish on 5/4/17.
 */
@Component
public class ProductClient {
    private static final String PRODUCT_SERVICE_URI = "http://product-service/products";

    private final RestTemplate restTemplate;

    @Autowired
    public ProductClient(final RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    /**
     * Creates a new Product in the product service.
     *
     * @param product The product which should be stored in the product service.
     * @param userId The user who want't to store the given product.
     * @return The stored product.
     */
    public Product createProduct(final Product product, final int userId) {
        HttpEntity<Product> request = new HttpEntity<>(product, createHeaderWithUserId(userId));
        return restTemplate.postForObject(PRODUCT_SERVICE_URI, request, Product.class);
    }

    /**
     * @param query
     * @param minPrice
     * @param maxPrice
     * @return
     */
    public Collection<Product> getProducts(final String query, final Integer minPrice, final Integer maxPrice) {
        final String _query;
        if (query == null) _query = "";
        else _query = query.trim();

        final double _minPrice;
        if (minPrice == null) _minPrice = Double.MIN_VALUE;
        else _minPrice = minPrice;

        final double _maxPrice;
        if (maxPrice == null) _maxPrice = Double.MAX_VALUE;
        else _maxPrice = maxPrice;

        final URI productsURI = UriComponentsBuilder.fromHttpUrl(PRODUCT_SERVICE_URI)
                                                    .queryParam("query", _query)        //
                                                    .queryParam("minPrice", _minPrice)  //
                                                    .queryParam("maxPrice", _maxPrice)  //
                                                    .build()                                  //
                                                    .encode()                                 //
                                                    .toUri();                                 //

        final Product[] products = restTemplate.getForObject(productsURI, Product[].class);

        return Stream.of(products)                    //
                     .map(ProductClient::addCategory) //
                     .collect(Collectors.toList());   //
    }

    /**
     * Tries to retrieve aproductt with the given id.
     * If not product could be found with the given Id null will be returned.
     *
     * @param id The id of the product which should be retired.
     * @return The Product or null if no product could be found.
     */
    public Product getProduct(final int id) {
        final Product product = restTemplate.getForObject(PRODUCT_SERVICE_URI + id, Product.class);
        if (product == null)  return null;

        addCategoryToProduct(product);
        // productCache.putIfAbsent(productId, tmpProduct);
        return product;
    }

    /**
     * Deletes a product with the given id.
     */
    public ResponseEntity<Void> deleteProduct(final int id, final int userId) {
        HttpEntity<?> request = new HttpEntity<>(createHeaderWithUserId(userId));
        return restTemplate.exchange(PRODUCT_SERVICE_URI + "{productId}", HttpMethod.DELETE, request, Void.class, id);
    }

    // -------------------------------------------- Private-Helper ------------------------------------------------------

    // Convenience wrapper for streams
    private static Product addCategory(final Product product) {
        addCategoryToProduct(product);
        return product;
    }

    private static void addCategoryToProduct(Product product) {
        // !TODO error if no category with given id found
        findCategory(product.getCategoryId())
                .map(Category::getName)                //
                .ifPresent(product::setCategoryName);  //
    }

    private static Optional<Category> findCategory(final int id) {
        return Optional.empty();
    }

}
