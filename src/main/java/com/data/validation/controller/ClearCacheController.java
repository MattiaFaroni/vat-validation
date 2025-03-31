package com.data.validation.controller;

import com.data.validation.api.ClearCacheInterface;
import com.data.validation.listener.ApplicationListener;
import com.data.validation.logging.Logger;
import com.data.validation.model.wrapper.ClearCacheRequest;
import com.data.validation.model.wrapper.ClearCacheResponse;
import com.data.validation.redis.RedisCacheManager;
import io.sentry.Sentry;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.InternalServerErrorException;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;

@Path("/cache/clear")
public class ClearCacheController extends Logger implements ClearCacheInterface {

    RedisCacheManager cacheManager = ApplicationListener.cacheManager;

    @Override
    // spotless:off
    public ClearCacheResponse clearCache(ClearCacheRequest clearCacheRequest) {

        if (clearCacheRequest != null) {
            if (clearCacheRequest.getIso2() != null
                    && !clearCacheRequest.getIso2().isEmpty()
                    && clearCacheRequest.getVatNumber() != null
                    && !clearCacheRequest.getVatNumber().isEmpty()) {

                try {
                    cacheManager.clearCache(clearCacheRequest.getIso2() + ":" + clearCacheRequest.getVatNumber());
                    return generateResponseSuccess();

                } catch (Exception e) {
                    Sentry.captureException(e);
                    printError("Error while clearing Redis cache", e.getMessage());
                    throw new InternalServerErrorException(Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                            .entity(generateResponseError(ClearCacheResponse.MessageEnum.REDIS_EXCEPTION))
                            .build());
                }

            } else {
                throw new BadRequestException(Response.status(Response.Status.BAD_REQUEST)
                        .entity(generateResponseError(ClearCacheResponse.MessageEnum.INCORRECT_INPUT_PARAMETERS))
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
                        .entity(generateResponseError(ClearCacheResponse.MessageEnum.REDIS_EXCEPTION))
                        .build());
            }
        }
    }
    // spotless:on

    /**
     * Method used to generate the response on success
     * @return service response
     */
    private ClearCacheResponse generateResponseSuccess() {
        ClearCacheResponse clearCacheResponse = new ClearCacheResponse();
        clearCacheResponse.setStatus(ClearCacheResponse.StatusEnum.SUCCESS);
        clearCacheResponse.setMessage(ClearCacheResponse.MessageEnum.CACHE_CLEARED_SUCCESSFULLY);
        return clearCacheResponse;
    }

    /**
     * Method used to generate the response in case of error
     * @param message error message
     * @return service response
     */
    private ClearCacheResponse generateResponseError(ClearCacheResponse.MessageEnum message) {
        ClearCacheResponse clearCacheResponse = new ClearCacheResponse();
        clearCacheResponse.setStatus(ClearCacheResponse.StatusEnum.ERROR);
        clearCacheResponse.setMessage(message);
        return clearCacheResponse;
    }
}
