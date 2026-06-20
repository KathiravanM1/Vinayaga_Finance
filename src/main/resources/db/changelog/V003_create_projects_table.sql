--liquibase formatted sql

--changeset kathiravan:001

CREATE TABLE projects (
                          project_id BIGSERIAL PRIMARY KEY,

                          project_code VARCHAR(50) NOT NULL UNIQUE,

                          project_name VARCHAR(255) NOT NULL,

                          client_name VARCHAR(255) NOT NULL,

                          location VARCHAR(255),

                          contract_value NUMERIC(15,2),

                          start_date DATE NOT NULL,

                          expected_end_date DATE,

                          project_status VARCHAR(20) NOT NULL
                              CHECK (
                                  project_status IN (
                                                     'ACTIVE',
                                                     'COMPLETED',
                                                     'ON_HOLD',
                                                     'CANCELLED'
                                      )
                                  ),

                          notes TEXT,

                          created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

                          updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
