package de.hska.muon.controller;

import de.hska.muon.client.CategoryClient;
import de.hska.muon.client.ProductClient;
import de.hska.muon.model.Category;
import de.hska.muon.model.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * This class represents the rest-api of the product and category composite-service.
 *
 * Created by amish on 5/4/17.
 */
@RestController
public class ProductCategoryService {

    private static final ResponseEntity<Category> CATEGORY_NOT_FOUND = new ResponseEntity<>(HttpStatus.NOT_FOUND);
    private final ProductClient products;
    private final CategoryClient categories;

    @Autowired
    public ProductCategoryService(final ProductClient products, final CategoryClient categories) {
        this.products = products;
        this.categories = categories;
    }

    // -- Product-API
    @RequestMapping(value = "/products", method = RequestMethod.GET)
    public ResponseEntity<Iterable<Product>> getProducts(@RequestParam(value = "query", required = false) final String query,
                                                         @RequestParam(value = "minPrice", required = false) final Integer minPrice,
                                                         @RequestParam(value = "maxPrice", required = false) final Integer maxPrice) {
        return new ResponseEntity<>(products.getProducts(query, minPrice, maxPrice), HttpStatus.OK);
    }

    @RequestMapping(value = "/products/{id}", method = RequestMethod.GET)
    public ResponseEntity<Product> getProduct(@PathVariable final String id) {
        return new ResponseEntity<>(products.getProduct(id), HttpStatus.OK);
    }

    @RequestMapping(value = "/products/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<Void> deleteProduct(@RequestHeader(value = "userId") final Integer userId,
                                              @PathVariable final String id) {
        return products.deleteProduct(id, userId);
    }

    @RequestMapping(value = "/products", method = RequestMethod.POST)
    public ResponseEntity<Product> createProduct(@RequestHeader(value = "userId") final Integer userId,
                                                 @RequestBody final Product product) {
        return new ResponseEntity<>(products.createProduct(product, userId), HttpStatus.OK);
    }

    // -- Category-API
    @RequestMapping(value = "/categories", method = RequestMethod.GET)
    public ResponseEntity<Iterable<Category>> getCategories() {
        return new ResponseEntity<>(categories.getCategories(), HttpStatus.OK);
    }

    @RequestMapping(value = "/categories/{id}", method = RequestMethod.GET)
    public ResponseEntity<Category> getCategory(@PathVariable final Integer id) {
        return categories.getCategory(id)
                         .map(ProductCategoryService::toOkEntity)  //
                         .orElse(CATEGORY_NOT_FOUND);              //
    }

    @RequestMapping(value = "/categories/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<Void> deleteCategory(@RequestHeader(value = "userId") final Integer userId,
                                               @PathVariable final Integer id) {

        if (products.isCategoryUsed(id)) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }

        return categories.deleteCategory(id, userId);
    }

    @RequestMapping(value = "/categories", method = RequestMethod.POST, produces = { "application/json" })
    public ResponseEntity<Category> createCategory(@RequestHeader(value = "userId") final Integer userId,
                                                   @RequestBody final Category category) {
        return new ResponseEntity<>(categories.createCategory(category, userId), HttpStatus.OK);
    }

    // ------------------------------------- Private-Api----------------------------------------------------------------

    private static ResponseEntity<Category> toOkEntity(final Category category) {
        return new ResponseEntity<>(category, HttpStatus.OK);
    }
}
