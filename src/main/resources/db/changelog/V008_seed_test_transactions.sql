--liquibase formatted sql

--changeset kathiravan:001

-- ============================================================
-- Expected totals for verification:
--
-- PRJ-000001 (project_id=1):
--   Income  = 100000 + 150000 + 100000 = 350000
--   Expense = 60000  + 75000  + 45000  = 180000
--   Profit  = 170000
--
-- PRJ-000002 (project_id=2):
--   Income  = 200000 + 220000 + 200000 = 620000
--   Expense = 120000 + 90000  + 100000 = 310000
--   Profit  = 310000
--
-- PRJ-000003 (project_id=3):
--   Income  = 80000  + 150000          = 230000
--   Expense = 70000  + 40000  + 40000  = 150000
--   Profit  = 80000
--
-- GLOBAL:
--   Total Income  = 1200000
--   Total Expense = 640000
--   Net Balance   = 560000
-- ============================================================

INSERT INTO transactions (
    project_id,
    category_id,
    amount,
    description,
    payment_mode,
    transaction_date,
    remarks,
    created_at
)
VALUES

-- ---- PRJ-000001 INCOME ----
(
    (SELECT project_id FROM projects WHERE project_code = 'PRJ-000001'),
    (SELECT category_id FROM categories WHERE category_name = 'ADVANCE_PAYMENT'),
    100000.00,
    'Advance payment received from client',
    'BANK_TRANSFER',
    '2025-01-05',
    'Initial advance 10%',
    NOW()
),
(
    (SELECT project_id FROM projects WHERE project_code = 'PRJ-000001'),
    (SELECT category_id FROM categories WHERE category_name = 'STAGE_PAYMENT'),
    150000.00,
    'Foundation stage payment',
    'CHEQUE',
    '2025-03-10',
    'Foundation completed',
    NOW()
),
(
    (SELECT project_id FROM projects WHERE project_code = 'PRJ-000001'),
    (SELECT category_id FROM categories WHERE category_name = 'STAGE_PAYMENT'),
    100000.00,
    'Slab stage payment',
    'BANK_TRANSFER',
    '2025-05-20',
    'Ground floor slab done',
    NOW()
),

-- ---- PRJ-000001 EXPENSE ----
(
    (SELECT project_id FROM projects WHERE project_code = 'PRJ-000001'),
    (SELECT category_id FROM categories WHERE category_name = 'CEMENT'),
    60000.00,
    'Cement purchase - 300 bags',
    'BANK_TRANSFER',
    '2025-01-15',
    'Ramco OPC 53 grade',
    NOW()
),
(
    (SELECT project_id FROM projects WHERE project_code = 'PRJ-000001'),
    (SELECT category_id FROM categories WHERE category_name = 'LABOUR'),
    75000.00,
    'Labour wages - January',
    'CASH',
    '2025-01-31',
    '15 workers x 5000',
    NOW()
),
(
    (SELECT project_id FROM projects WHERE project_code = 'PRJ-000001'),
    (SELECT category_id FROM categories WHERE category_name = 'STEEL'),
    45000.00,
    'Steel rods - 500kg',
    'UPI',
    '2025-02-10',
    'TMT bars Fe 500',
    NOW()
),

-- ---- PRJ-000002 INCOME ----
(
    (SELECT project_id FROM projects WHERE project_code = 'PRJ-000002'),
    (SELECT category_id FROM categories WHERE category_name = 'ADVANCE_PAYMENT'),
    200000.00,
    'Advance payment - commercial project',
    'BANK_TRANSFER',
    '2025-02-05',
    '20 lakh advance',
    NOW()
),
(
    (SELECT project_id FROM projects WHERE project_code = 'PRJ-000002'),
    (SELECT category_id FROM categories WHERE category_name = 'STAGE_PAYMENT'),
    220000.00,
    'Stage 1 - Piling work complete',
    'CHEQUE',
    '2025-04-15',
    'Piling and foundation done',
    NOW()
),
(
    (SELECT project_id FROM projects WHERE project_code = 'PRJ-000002'),
    (SELECT category_id FROM categories WHERE category_name = 'STAGE_PAYMENT'),
    200000.00,
    'Stage 2 - Ground floor slab',
    'BANK_TRANSFER',
    '2025-06-20',
    'GF slab poured',
    NOW()
),

-- ---- PRJ-000002 EXPENSE ----
(
    (SELECT project_id FROM projects WHERE project_code = 'PRJ-000002'),
    (SELECT category_id FROM categories WHERE category_name = 'CEMENT'),
    120000.00,
    'Cement - 600 bags',
    'BANK_TRANSFER',
    '2025-02-15',
    'UltraTech bulk purchase',
    NOW()
),
(
    (SELECT project_id FROM projects WHERE project_code = 'PRJ-000002'),
    (SELECT category_id FROM categories WHERE category_name = 'MACHINERY'),
    90000.00,
    'Crane rental - 30 days',
    'BANK_TRANSFER',
    '2025-03-01',
    'JCB and crane charges',
    NOW()
),
(
    (SELECT project_id FROM projects WHERE project_code = 'PRJ-000002'),
    (SELECT category_id FROM categories WHERE category_name = 'LABOUR'),
    100000.00,
    'Labour wages - March',
    'CASH',
    '2025-03-31',
    '20 workers x 5000',
    NOW()
),

-- ---- PRJ-000003 INCOME ----
(
    (SELECT project_id FROM projects WHERE project_code = 'PRJ-000003'),
    (SELECT category_id FROM categories WHERE category_name = 'ADVANCE_PAYMENT'),
    80000.00,
    'Advance for warehouse project',
    'BANK_TRANSFER',
    '2024-10-05',
    NULL,
    NOW()
),
(
    (SELECT project_id FROM projects WHERE project_code = 'PRJ-000003'),
    (SELECT category_id FROM categories WHERE category_name = 'FINAL_PAYMENT'),
    150000.00,
    'Final payment - warehouse handover',
    'CHEQUE',
    '2025-06-15',
    'Project completed and handed over',
    NOW()
),

-- ---- PRJ-000003 EXPENSE ----
(
    (SELECT project_id FROM projects WHERE project_code = 'PRJ-000003'),
    (SELECT category_id FROM categories WHERE category_name = 'SAND'),
    70000.00,
    'Sand - 20 loads',
    'CASH',
    '2024-10-20',
    NULL,
    NOW()
),
(
    (SELECT project_id FROM projects WHERE project_code = 'PRJ-000003'),
    (SELECT category_id FROM categories WHERE category_name = 'ELECTRICAL'),
    40000.00,
    'Electrical wiring - warehouse',
    'UPI',
    '2025-05-10',
    NULL,
    NOW()
),
(
    (SELECT project_id FROM projects WHERE project_code = 'PRJ-000003'),
    (SELECT category_id FROM categories WHERE category_name = 'TRANSPORT'),
    40000.00,
    'Material transport charges',
    'CASH',
    '2025-05-25',
    NULL,
    NOW()
)

ON CONFLICT DO NOTHING;
