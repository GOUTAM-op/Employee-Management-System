package com.empmgmt.exception;

/**
 * Wraps low-level java.sql.SQLException into an unchecked, application-specific
 * exception carrying a friendly, user-safe message plus the original SQLException
 * (with its vendor error code) for logging/diagnostics.
 */
public class DatabaseException extends RuntimeException {

    private final int sqlErrorCode;

    public DatabaseException(String userMessage, Throwable cause) {
        super(userMessage, cause);
        int code = -1;
        if (cause instanceof java.sql.SQLException) {
            code = ((java.sql.SQLException) cause).getErrorCode();
        }
        this.sqlErrorCode = code;
    }

    public int getSqlErrorCode() { return sqlErrorCode; }
}
