--liquibase formatted sql

--changeset kathiravan:001


CREATE TABLE transactions (
              transaction_id BIGSERIAL PRIMARY KEY,

              project_id BIGINT NOT NULL,

              category_id BIGINT NOT NULL,

              amount NUMERIC(15,2) NOT NULL
                  CHECK (amount > 0),

              description TEXT,

              payment_mode VARCHAR(30)
                  CHECK (
                      payment_mode IN (
                                       'CASH',
                                       'UPI',
                                       'BANK_TRANSFER',
                                       'CHEQUE'
                          )
                      ),

              transaction_date DATE NOT NULL,

              remarks TEXT,

              attachment_url VARCHAR(500),

              created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

              CONSTRAINT fk_transaction_project
                  FOREIGN KEY (project_id)
                      REFERENCES projects(project_id),

              CONSTRAINT fk_transaction_category
                  FOREIGN KEY (category_id)
                      REFERENCES categories(category_id)
);

