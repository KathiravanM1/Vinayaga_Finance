--liquibase formatted sql

--changeset kathiravan:001
DELETE FROM transactions
WHERE project_id IN (
    SELECT project_id FROM projects
    WHERE project_code IN ('PRJ-000001', 'PRJ-000002', 'PRJ-000003')
);

DELETE FROM projects
WHERE project_code IN ('PRJ-000001', 'PRJ-000002', 'PRJ-000003');
