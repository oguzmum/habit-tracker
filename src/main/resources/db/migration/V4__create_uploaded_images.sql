CREATE TABLE uploaded_images (
    id BIGSERIAL PRIMARY KEY,
    original_filename VARCHAR(255) NOT NULL,
    stored_filename VARCHAR(255) NOT NULL UNIQUE,
    storage_path VARCHAR(1024) NOT NULL,
    uploaded_at TIMESTAMP NOT NULL
);
