package lk.ijse.dep9.dao.custom.exception;

import java.sql.SQLException;

public class ConstraintViolationException extends RuntimeException {
    public ConstraintViolationException(String reason, Throwable cause) {
        super(reason, cause);
    }
}
