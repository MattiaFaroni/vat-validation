package com.data.validation.controller;

import com.data.validation.api.ClearCacheInterface;
import com.data.validation.listener.ApplicationListener;
import com.data.validation.logging.Logger;
import com.data.validation.model.wrapper.ClearCacheRequest;
import com.data.validation.model.wrapper.ClearCacheResponse;
import com.data.validation.model.wrapper.Key;
import com.data.validation.redis.RedisCacheManager;
import io.sentry.Sentry;
import jakarta.validation.Valid;
import jakarta.ws.rs.InternalServerErrorException;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;

@Path("/cache/clear")
public class ClearCacheController extends Logger implements ClearCacheInterface {

    // spotless:off
    RedisCacheManager cacheManager = ApplicationListener.cacheManager;

    @Override
    public ClearCacheResponse clearCache(ClearCacheRequest clearCacheRequest) {

        if (clearCacheRequest != null) {

            try {
                for (@Valid Key key : clearCacheRequest.getKeys()) {
                    cacheManager.clearCache(key.getIso2() + ":" + key.getVatNumber());
                }
                return generateResponseSuccess();

            } catch (Exception e) {
                Sentry.captureException(e);
                printError("Error while clearing Redis cache", e.getMessage());
                throw new InternalServerErrorException(Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                        .entity(generateResponseError(e.getMessage()))
                        .build());
            }

        } else {

            try {
                cacheManager.clearAllCache();
                return generateResponseSuccess();

            } catch (Exception e) {
                Sentry.captureException(e);
                printError("Error while clearing Redis cache", e.getMessage());
                throw new InternalServerErrorException(Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                        .entity(generateResponseError(e.getMessage()))
                        .build());
            }
        }
    }
    // spotless:on

    /**
     * Generates a response indicating a successful cache clearing operation.
     * @return a ClearCacheResponse object with status set to SUCCESS and a success message
     */
    private ClearCacheResponse generateResponseSuccess() {
        ClearCacheResponse clearCacheResponse = new ClearCacheResponse();
        clearCacheResponse.setStatus(ClearCacheResponse.StatusEnum.SUCCESS);
        clearCacheResponse.setMessage("Cache cleared successfully");
        return clearCacheResponse;
    }

    /**
     * Generates a response indicating an error during the cache clearing process.
     * @param message the error message describing the issue
     * @return a ClearCacheResponse object with status set to ERROR and the provided error message
     */
    private ClearCacheResponse generateResponseError(String message) {
        ClearCacheResponse clearCacheResponse = new ClearCacheResponse();
        clearCacheResponse.setStatus(ClearCacheResponse.StatusEnum.ERROR);
        clearCacheResponse.setMessage(message);
        return clearCacheResponse;
    }
}
