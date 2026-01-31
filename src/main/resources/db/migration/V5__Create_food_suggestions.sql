-- Flyway migration: create food_suggestions table
CREATE TABLE IF NOT EXISTS food_suggestions (
  id BIGSERIAL PRIMARY KEY,
  user_id BIGINT NOT NULL,
  name VARCHAR(255) NOT NULL,
  description TEXT,
  price NUMERIC(12,2) NOT NULL,
  category VARCHAR(255) NOT NULL,
  image_url VARCHAR(1000),
  status VARCHAR(50) DEFAULT 'PENDING',
  admin_note TEXT,
  created_at TIMESTAMP WITHOUT TIME ZONE DEFAULT now()
);

CREATE INDEX IF NOT EXISTS idx_food_suggestions_user_id ON food_suggestions(user_id);
