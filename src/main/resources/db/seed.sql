insert into roles(name, description, created_at, updated_at) values
('admin', 'Administrator role with all permissions', now(), now()),
('user', 'Regular user role with limited permissions', now(), now()),
('guest', 'Guest role with minimal permissions', now(), now());