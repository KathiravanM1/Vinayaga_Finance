--liquibase formatted sql

--changeset kathiravan:001

CREATE TABLE categories (
                            category_id BIGSERIAL PRIMARY KEY,

                            category_name VARCHAR(100) NOT NULL,

                            category_type VARCHAR(20) NOT NULL
                                CHECK (category_type IN ('INCOME', 'EXPENSE')),

                            created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);