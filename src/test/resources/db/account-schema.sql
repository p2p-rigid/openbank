CREATE EXTENSION IF NOT EXISTS pgcrypto;

CREATE SCHEMA IF NOT EXISTS openbank;

CREATE TABLE openbank.account (
                                  account_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                                  account_number VARCHAR(100) NOT NULL,
                                  owner_name VARCHAR(255) NOT NULL,
                                  balance NUMERIC(19, 2) NOT NULL DEFAULT 0.00,
                                  currency CHAR(3) NOT NULL,
                                  created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT now(),
                                  status VARCHAR(16) NOT NULL DEFAULT 'ACTIVE'
);