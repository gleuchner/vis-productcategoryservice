package de.hska.muon.client;

import com.googlecode.cqengine.ConcurrentIndexedCollection;
import com.googlecode.cqengine.IndexedCollection;
import com.googlecode.cqengine.index.navigable.NavigableIndex;
import com.googlecode.cqengine.query.Query;
import com.googlecode.cqengine.resultset.ResultSet;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import de.hska.muon.model.Category;
import de.hska.muon.model.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.googlecode.cqengine.query.QueryFactory.equal;
import static de.hska.muon.client.Clients.createHeaderWithUserId;

/**
 * This class is taking care of redirecting all the calls to the product-service.
 * <p>
 * Created by amish on 5/4/17.
 */
@Component
public class ProductClient {
    private static final String PRODUCT_SERVICE_URI = "http://product-service/products";

    private final RestTemplate restTemplate;

    // TODO: Is that the way to go?
    /*
     * caches all products.
     */
    private final IndexedCollection<Product> products = new ConcurrentIndexedCollection<>();


    @Autowired
    public ProductClient(final RestTemplate oAuthRestTemplate) {
        this.restTemplate = oAuthRestTemplate;
        products.addIndex(NavigableIndex.onAttribute(Product.CATEGORY_ID));
    }

    /**
     * Creates a new Product in the product service.
     *
     * @param product The product which should be stored in the product service.
     * @param userId  The user who want't to store the given product.
     * @return The stored product.
     */
    @HystrixCommand
    public Product createProduct(final Product product, final Integer userId) {
        // TODO: Is the categoryId set here already or do we have to set it manually?
        HttpEntity<Product> request = new HttpEntity<>(product, createHeaderWithUserId(userId));
        Product createdProduct = restTemplate.postForObject(PRODUCT_SERVICE_URI, request, Product.class);
        products.add(createdProduct);
        return createdProduct;
    }


    /**
     * @param query
     * @param minPrice
     * @param maxPrice
     * @return
     */
    @HystrixCommand(fallbackMethod = "getProductsCache")
    public Collection<Product> getProducts(final String query, final Integer minPrice, final Integer maxPrice) {
        final String _query;
        if (query == null) _query = "";
        else _query = query.trim();

        final Integer _minPrice;
        if (minPrice == null) _minPrice = Integer.MIN_VALUE;
        else _minPrice = minPrice;

        final Integer _maxPrice;
        if (maxPrice == null) _maxPrice = Integer.MAX_VALUE;
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

    public Collection<Product> getProductsCache(final String query, final Integer minPrice, final Integer maxPrice) {
        return new ArrayList<Product>();
    }


    /**
     * Tries to retrieve aproductt with the given id.
     * If not product could be found with the given Id null will be returned.
     *
     * @param id The id of the product which should be retired.
     * @return The Product or null if no product could be found.
     */
    @HystrixCommand
    public Product getProduct(final Integer id) {
        final Product product = restTemplate.getForObject(PRODUCT_SERVICE_URI + "/{productId}", Product.class, id);
        if (product == null) return null;

        addCategoryToProduct(product);
        // TODO: Test if this is really working right!
        products.update(Arrays.asList(product), Arrays.asList(product));
        return product;
    }

    /**
     * Deletes a product with the given id.
     */
    @HystrixCommand
    public ResponseEntity<Void> deleteProduct(final Integer id, final Integer userId) {
        products.removeIf(product -> product.getProductId().intValue() == id.intValue());
        HttpEntity<HttpHeaders> request = new HttpEntity<>(createHeaderWithUserId(userId));
        return restTemplate.exchange(PRODUCT_SERVICE_URI + "/{productId}", HttpMethod.DELETE, request, Void.class, id);
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
                .map(Category::getName);             //
    }

    private static Optional<Category> findCategory(final Integer id) {
        return Optional.empty();
    }

    public boolean isCategoryUsed(final Integer id) {
        Query<Product> query = equal(Product.CATEGORY_ID, id);
        try (ResultSet<Product> rs = products.retrieve(query)) {
            return rs.size() > 0;
        }
    }


}
