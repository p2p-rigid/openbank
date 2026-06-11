package com.example.openbank.repository;

public class Q {

  public static final String INSERT_ACCOUNT_QUERY =
      """
            INSERT INTO %s (account_number, owner_name, currency)
            VALUES (:account_number, :owner_name, :currency)
            RETURNING account_id
            """;

  public static final String IS_ACCOUNT_EXISTS_QUERY =
      """
                  SELECT EXISTS (
                      SELECT 1
                      FROM account
                      WHERE account_number = :account_number
                  ) AS exists
                  """;
}
