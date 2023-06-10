/* (C)2023 */
package com.github.caiosilva.hibp.api;

import com.github.caiosilva.hibp.entity.Breach;
import com.github.caiosilva.hibp.entity.Paste;
import com.github.caiosilva.hibp.entity.PwnedHash;
import com.github.caiosilva.hibp.exception.HaveIBeenPwndException;
import com.github.caiosilva.hibp.validation.APIKeyValidation;
import com.github.caiosilva.hibp.validation.ResponseValidation;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

public interface HaveIBeenPwndAPI extends ResponseValidation, APIKeyValidation {

    List<Breach> getAllBreachesForAccount(String account)
            throws HaveIBeenPwndException, IOException;

    List<Breach> getAllBreachedSites() throws HaveIBeenPwndException, IOException;

    List<Breach> getAllBreachedSites(String domain) throws HaveIBeenPwndException, IOException;

    Optional<Breach> getBreachByName(String breach) throws HaveIBeenPwndException;

    List<String> getAllDataClasses() throws HaveIBeenPwndException;

    List<Paste> getAllPastesForAccount(String account) throws HaveIBeenPwndException;

    List<PwnedHash> searchByRange(String hash5) throws HaveIBeenPwndException;

    boolean isAccountPwned(String account) throws HaveIBeenPwndException;

    boolean isPlainPasswordPwned(String password) throws HaveIBeenPwndException;

    boolean isHashPasswordPwned(String pwHash) throws HaveIBeenPwndException;

    String makeHash(String password);

    void validateAPIKey() throws HaveIBeenPwndException;
}
