-- Create relationship_types table
CREATE TABLE relationship_types (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    category VARCHAR(50) NOT NULL,
    bidirectional BOOLEAN NOT NULL DEFAULT false,
    reverse_type_id BIGINT REFERENCES relationship_types(id),
    metadata JSONB,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Create indexes for relationship_types
CREATE UNIQUE INDEX idx_relationship_types_id ON relationship_types(id);
CREATE UNIQUE INDEX idx_relationship_types_name ON relationship_types(name);
CREATE INDEX idx_relationship_types_category ON relationship_types(category);
CREATE INDEX idx_relationship_types_bidirectional ON relationship_types(bidirectional);
CREATE INDEX idx_relationship_types_reverse_type_id ON relationship_types(reverse_type_id);

-- Add check constraints
ALTER TABLE relationship_types ADD CONSTRAINT chk_relationship_types_category 
    CHECK (category IN ('FAMILY', 'SOCIAL', 'PROFESSIONAL', 'CUSTOM'));

-- Add comments
COMMENT ON TABLE relationship_types IS 'Defines all available relationship types in the system';
COMMENT ON COLUMN relationship_types.name IS 'Unique name of the relationship type (e.g., "Father", "Son", "Spouse")';
COMMENT ON COLUMN relationship_types.category IS 'Category of relationship (FAMILY, SOCIAL, PROFESSIONAL, CUSTOM)';
COMMENT ON COLUMN relationship_types.bidirectional IS 'Whether this relationship type works both ways';
COMMENT ON COLUMN relationship_types.reverse_type_id IS 'Reference to the reverse relationship type (e.g., Father → Son)';
COMMENT ON COLUMN relationship_types.metadata IS 'JSON field for additional properties and descriptions';
