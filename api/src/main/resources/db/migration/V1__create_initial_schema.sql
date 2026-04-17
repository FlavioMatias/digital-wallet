CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- 1. Tabela de Users
CREATE TABLE users (
                       id UUID PRIMARY KEY,
                       email VARCHAR(255) NOT NULL UNIQUE,
                       password VARCHAR(255) NOT NULL,
                       created_at TIMESTAMP NOT NULL,
                       updated_at TIMESTAMP NOT NULL,
                       version BIGINT
);

-- 2. Tabela de Profiles
CREATE TABLE profiles (
                          id UUID PRIMARY KEY,
                          first_name VARCHAR(255) NOT NULL,
                          last_name VARCHAR(255) NOT NULL,
                          document VARCHAR(14) NOT NULL UNIQUE,
                          profile_role VARCHAR(20) NOT NULL,
                          user_id UUID UNIQUE,
                          created_at TIMESTAMP NOT NULL,
                          updated_at TIMESTAMP NOT NULL,
                          version BIGINT,
                          CONSTRAINT fk_profile_user FOREIGN KEY (user_id) REFERENCES users(id)
);

-- 3. Tabela de Wallets
CREATE TABLE wallets (
                         id UUID PRIMARY KEY,
                         available_balance DECIMAL(19, 4) NOT NULL,
                         blocked_balance DECIMAL(19, 4) NOT NULL,
                         currency VARCHAR(10) NOT NULL,
                         status VARCHAR(20) NOT NULL,
                         owner_id UUID,
                         created_at TIMESTAMP NOT NULL,
                         updated_at TIMESTAMP NOT NULL,
                         version BIGINT
);

-- 4. Tabela de Transactions
CREATE TABLE transactions (
                              id UUID PRIMARY KEY,
                              amount DECIMAL(19, 4) NOT NULL,
                              currency VARCHAR(10) NOT NULL,
                              correlation_id UUID,
                              wallet_id UUID NOT NULL,
                              type VARCHAR(20) NOT NULL,
                              status VARCHAR(20) NOT NULL,
                              created_at TIMESTAMP NOT NULL,
                              updated_at TIMESTAMP NOT NULL,
                              version BIGINT,
                              CONSTRAINT fk_transaction_wallet FOREIGN KEY (wallet_id) REFERENCES wallets(id)
);

-- 5. Tabela de Outbox
CREATE TABLE outbox_events (
                               id UUID PRIMARY KEY,
                               aggregate_type VARCHAR(255) NOT NULL,
                               event_type VARCHAR(255) NOT NULL,
                               payload TEXT NOT NULL,
                               processed_at TIMESTAMP,
                               created_at TIMESTAMP NOT NULL,
                               updated_at TIMESTAMP NOT NULL,
                               version BIGINT
);