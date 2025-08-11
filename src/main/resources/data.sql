-- Insert default roles if they don't exist
INSERT IGNORE INTO roles (id, name) VALUES 
(1, 'admin'),
(2, 'owner'),
(3, 'pm'),
(4, 'leader'),
(5, 'member');