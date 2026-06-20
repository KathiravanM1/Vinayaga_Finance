--liquibase formatted sql

--changeset kathiravan:001
CREATE INDEX idx_transactions_project_id
    ON transactions(project_id);

--changeset kathiravan:002
CREATE INDEX idx_transactions_category_id
    ON transactions(category_id);

--changeset kathiravan:003
CREATE INDEX idx_transactions_date
    ON transactions(transaction_date);

--changeset kathiravan:004
CREATE INDEX idx_projects_status
    ON projects(project_status);
