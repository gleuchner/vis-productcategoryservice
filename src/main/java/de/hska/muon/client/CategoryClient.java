package de.hska.muon.client;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import de.hska.muon.model.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;

import static de.hska.muon.client.Clients.createHeaderWithUserId;

/**
 * Created by amish on 5/6/17.
 */
@Component
public class CategoryClient {
    private static final String CATEGORY_SERVICE_URL = "http://category-service/categories";

    private final RestTemplate restTemplate;

    @Autowired
    public CategoryClient(final RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    // -- Category-Api
    public Collection<Category> getCategories() {
        final Category[] categories = restTemplate.getForObject(CATEGORY_SERVICE_URL, Category[].class);
        return Arrays.asList(categories);
    }

    /**
     * Creates a category.
     *
     * @param category The category that should be created.
     * @param userId The user who want's to create the category.
     *
     * @return The category.
     */
    @HystrixCommand
    public Category createCategory(final Category category, final int userId) {
        HttpEntity<Category> request = new HttpEntity<>(category, createHeaderWithUserId(userId));
        return restTemplate.postForObject(CATEGORY_SERVICE_URL, request, Category.class);
    }

    /**
     * Tries to retrieve the category for the given id.
     *
     * @param id The id of the category.
     *
     * @return The category if the category could be found, null otherwise.
     */
    @HystrixCommand
    public Optional<Category> getCategory(final int id) {
        return Optional.ofNullable(restTemplate.getForObject(CATEGORY_SERVICE_URL + "{categoryId}", Category.class, id));
    }

    /**
     * Deletes the category with the given id.
     *
     * @param id The id of the category, that should be deleted.
     * @param userId The user who want's to delete the given category.
     *
     * @return TODO:
     */
    @HystrixCommand
    public ResponseEntity<Void> deleteCategory(final int id, final int userId) {
        // 1. We need to delete all products which belong to the category

        // 2. We need to delete the category.

        HttpEntity<HttpHeaders> request = new HttpEntity<>(createHeaderWithUserId(userId));
        return restTemplate.exchange(CATEGORY_SERVICE_URL + "{categoryId}", HttpMethod.DELETE, request, Void.class, id);
    }

}
