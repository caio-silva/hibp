/* (C)2023 */
package com.github.caiosilva.hibp.executor;

import com.github.caiosilva.hibp.exception.HaveIBeenPwndException;
import com.github.caiosilva.hibp.validation.ResponseValidation;
import java.io.IOException;
import java.util.Optional;
import java.util.function.Consumer;
import lombok.experimental.UtilityClass;
import retrofit2.Call;
import retrofit2.Response;

@UtilityClass
public class HttpCallExecutor implements ResponseValidation {

    public static void callService(Call<?> call, Consumer<Response<?>> callBack)
            throws HaveIBeenPwndException {
        try {
            Response<?> response = call.execute();
            ResponseValidation.validate(response);
            callBack.accept(response);
        } catch (IOException e) {
            throw new HaveIBeenPwndException.IOException(e.getMessage(), e);
        }
    }

    public static <T> Optional<T> callService(Call<T> call) throws HaveIBeenPwndException {
        try {
            Response<T> res = call.execute();
            ResponseValidation.validate(res);
            return Optional.ofNullable(res.body());
        } catch (IOException e) {
            throw new HaveIBeenPwndException.IOException(e.getMessage(), e);
        }
    }
}
