-- Create ratings table
CREATE TABLE IF NOT EXISTS ratings (
  id BIGSERIAL PRIMARY KEY,
  food_id BIGINT NOT NULL,
  score INT NOT NULL CHECK (score >= 1 AND score <= 5),
  user_id BIGINT,
  comment TEXT,
  created_at TIMESTAMP WITHOUT TIME ZONE DEFAULT now(),
  updated_at TIMESTAMP WITHOUT TIME ZONE,
  CONSTRAINT uq_ratings_food_user UNIQUE (food_id, user_id),
  CONSTRAINT fk_ratings_food FOREIGN KEY(food_id) REFERENCES foods(id) ON DELETE CASCADE,
  CONSTRAINT fk_ratings_user FOREIGN KEY(user_id) REFERENCES users(id) ON DELETE SET NULL
);
