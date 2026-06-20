--liquibase formatted sql

--changeset kathiravan:001

INSERT INTO categories (
    category_name,
    category_type
)
VALUES
    ('ADVANCE_PAYMENT', 'INCOME'),
    ('STAGE_PAYMENT', 'INCOME'),
    ('FINAL_PAYMENT', 'INCOME'),
    ('OTHER_INCOME', 'INCOME'),

    ('CEMENT', 'EXPENSE'),
    ('STEEL', 'EXPENSE'),
    ('SAND', 'EXPENSE'),
    ('BRICKS', 'EXPENSE'),
    ('LABOUR', 'EXPENSE'),
    ('PLUMBING', 'EXPENSE'),
    ('ELECTRICAL', 'EXPENSE'),
    ('TRANSPORT', 'EXPENSE'),
    ('MACHINERY', 'EXPENSE'),
    ('OTHER_EXPENSE', 'EXPENSE') on conflict do nothing;