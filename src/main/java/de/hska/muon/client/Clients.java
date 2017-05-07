package de.hska.muon.client;

import org.springframework.http.HttpHeaders;

/**
 * Util class for the clients.
 *
 * Created by amish on 5/6/17.
 */
final class Clients {

    private Clients() { }

    /**
     * Creates a <code>{@link HttpHeaders}</code> with the given id set as the userId.
     *
     * @param id The Id which should be set in the header.
     *
     * @return The HttpHeaders-Object.
     */
    static HttpHeaders createHeaderWithUserId(final int id) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("userId", Integer.toString(id));
        return headers;
    }
}
