-- Add payment-related columns to orders table
ALTER TABLE orders ADD COLUMN payment_method VARCHAR(50) DEFAULT 'cash';
ALTER TABLE orders ADD COLUMN payment_intent_id VARCHAR(100);
ALTER TABLE orders ADD COLUMN payment_status VARCHAR(50);

-- Set default payment status for existing orders
UPDATE orders SET payment_status = 'pending' WHERE payment_status IS NULL;