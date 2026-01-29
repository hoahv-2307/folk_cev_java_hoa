-- Insert initial users
INSERT INTO users (username, email, password, role) VALUES 
('admin', 'admin@foods.com', '$2a$10$VlTf.QOT5/j6vZCsq3WKOO.mOvEzKWz5pPk4a8xrjzqP4TzBqQ7Ny', 'ADMIN'),
('user', 'user@foods.com', '$2a$10$VlTf.QOT5/j6vZCsq3WKOO.mOvEzKWz5pPk4a8xrjzqP4TzBqQ7Ny', 'USER');

-- Insert initial food items
INSERT INTO foods (name, description, category, price, quantity, status, created_at, updated_at) VALUES
('Margherita Pizza', 'Classic pizza with tomato sauce, mozzarella, and basil', 'Italian', 12.99, 10, 'ACTIVE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Beef Burger', 'Juicy beef patty with lettuce, tomato, and cheese', 'American', 9.99, 15, 'ACTIVE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Salmon Sushi', 'Fresh salmon sushi with rice and wasabi', 'Japanese', 15.50, 20, 'ACTIVE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Spaghetti Carbonara', 'Creamy pasta with bacon, eggs, and parmesan cheese', 'Italian', 11.75, 12, 'ACTIVE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Chicken Tacos', 'Soft tacos with grilled chicken, salsa, and avocado', 'Mexican', 8.25, 18, 'ACTIVE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);