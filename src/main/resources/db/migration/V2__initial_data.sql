-- Password value is a 12 Rounds Hash
INSERT INTO "app_user" (id, username, email, password, name, created_at, created_by, updated_at, updated_by, active)
VALUES (1, 'testuser', 'test@yopmail.com', '$2a$12$Soxl/mVuYHU2plATlf31zOqsn2pA9Avw.Or/cub61.fFe9ei3mwKS', 'Test Test', CURRENT_TIMESTAMP, 'system', CURRENT_TIMESTAMP, 'system', TRUE);

INSERT INTO "app_user" (id, username, email, password, name, created_at, created_by, updated_at, updated_by, active)
VALUES (2, 'secondtest', 'secondtest@yopmail.com', '$2a$12$Soxl/mVuYHU2plATlf31zOqsn2pA9Avw.Or/cub61.fFe9ei3mwKS', 'Second Test', CURRENT_TIMESTAMP, 'system', CURRENT_TIMESTAMP, 'system', TRUE);
