/* (C)2023 */
package com.github.caiosilva.hibp.executor;

import static com.github.caiosilva.hibp.executor.HttpCallExecutor.callService;

import com.github.caiosilva.hibp.client.HIBPHttpClient;
import com.github.caiosilva.hibp.entity.Breach;
import com.github.caiosilva.hibp.entity.Paste;
import com.github.caiosilva.hibp.exception.HaveIBeenPwndException;
import com.github.caiosilva.hibp.rateLimit.RateLimiterEntity;
import com.github.caiosilva.hibp.validation.ResponseValidation;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

public class RateLimiterHttpCallExecutor implements ResponseValidation {
    private final HIBPHttpClient hibpService;
    private final List<RateLimiterEntity> rateLimiter;

    public RateLimiterHttpCallExecutor(
            HIBPHttpClient hibpService, List<RateLimiterEntity> rateLimiterEntityList) {
        this.hibpService = hibpService;
        rateLimiter = rateLimiterEntityList;
    }

    public List<Paste> getAllPastesForAccount(String account) throws HaveIBeenPwndException {
        List<Paste> result = new ArrayList<>();
        boolean success = false;
        RateLimiterEntity rateLimiterEntity;
        String key;

        while (!success) {
            Iterator<RateLimiterEntity> it = rateLimiter.iterator();
            while (it.hasNext()) {
                rateLimiterEntity = it.next();
                if (rateLimiterEntity.getBucket().tryConsume(1)) {
                    try {
                        key = rateLimiterEntity.getApiAccount().getKey();

                        Optional<List<Paste>> pastes =
                                callService(hibpService.getAllPastesForAccount(key, account));
                        success = true;

                        if (pastes.isPresent()) {
                            result = pastes.get();
                        }
                    } catch (HaveIBeenPwndException.TooManyRequestsException e) {
                    }
                }
            }
        }

        return result;
    }

    public List<Breach> getAllBreachesForAccount(
            String account, String domain, boolean truncateResponse, boolean includeUnverified)
            throws HaveIBeenPwndException {
        List<Breach> result = new ArrayList<>();
        boolean success = false;
        RateLimiterEntity rateLimiterEntity;
        String key;

        Iterator<RateLimiterEntity> it = rateLimiter.iterator();
        while (it.hasNext() && !success) {
            rateLimiterEntity = it.next();
            if (rateLimiterEntity.getBucket().tryConsume(1)) {
                try {
                    key = rateLimiterEntity.getApiAccount().getKey();

                    Optional<List<Breach>> breaches =
                            callService(
                                    hibpService.getAllBreachesForAccount(
                                            key,
                                            account,
                                            includeUnverified,
                                            truncateResponse,
                                            domain));
                    success = true;

                    if (breaches.isPresent()) {
                        result = breaches.get();
                    }
                } catch (HaveIBeenPwndException.TooManyRequestsException e) {
                }
            }
        }

        return result;
    }
}
