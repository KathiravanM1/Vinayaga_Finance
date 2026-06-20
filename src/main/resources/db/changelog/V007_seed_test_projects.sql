--liquibase formatted sql

--changeset kathiravan:001

INSERT INTO projects (
    project_code,
    project_name,
    client_name,
    location,
    contract_value,
    start_date,
    expected_end_date,
    project_status,
    notes,
    created_at,
    updated_at
)
VALUES
    (
        'PRJ-000001',
        'Residential Villa - Anna Nagar',
        'Ramesh Kumar',
        'Chennai',
        4500000.00,
        '2025-01-01',
        '2025-12-31',
        'ACTIVE',
        'G+2 structure',
        NOW(),
        NOW()
    ),
    (
        'PRJ-000002',
        'Commercial Complex - T Nagar',
        'Selvam Builders',
        'Chennai',
        12000000.00,
        '2025-02-01',
        '2026-03-31',
        'ACTIVE',
        'Ground floor + 4 floors',
        NOW(),
        NOW()
    ),
    (
        'PRJ-000003',
        'Warehouse - Ambattur',
        'Sri Logistics Pvt Ltd',
        'Chennai',
        3200000.00,
        '2024-10-01',
        '2025-06-30',
        'COMPLETED',
        'Single floor industrial shed',
        NOW(),
        NOW()
    )
ON CONFLICT DO NOTHING;
