CREATE TABLE app_users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password_hash VARCHAR(100) NOT NULL
);

CREATE TABLE items (
    id UUID PRIMARY KEY,
    owner_id BIGINT NOT NULL REFERENCES app_users(id),
    name VARCHAR(120) NOT NULL,
    price BIGINT NOT NULL CHECK (price > 0),
    contacts BIGINT NOT NULL DEFAULT 0 CHECK (contacts >= 0),
    likes BIGINT NOT NULL DEFAULT 0 CHECK (likes >= 0),
    view_count BIGINT NOT NULL DEFAULT 0 CHECK (view_count >= 0)
);

CREATE INDEX idx_items_owner_id ON items(owner_id);
