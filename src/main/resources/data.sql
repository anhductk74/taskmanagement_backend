-- Insert default roles if they don't exist
INSERT IGNORE INTO roles (id, name) VALUES 
(1, 'MEMBER'),
(2, 'LEADER'),
(3, 'PROJECT_MANAGER'),
(4, 'OWNER'),
(5, 'ADMIN');