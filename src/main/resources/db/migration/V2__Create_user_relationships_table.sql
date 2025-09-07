-- Create user_relationships table
CREATE TABLE user_relationships (
    id BIGSERIAL PRIMARY KEY,
    user1_id BIGINT NOT NULL,
    user2_id BIGINT NOT NULL,
    relationship_type_id BIGINT NOT NULL REFERENCES relationship_types(id),
    context_id BIGINT,
    start_date DATE,
    end_date DATE,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    metadata JSONB,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    -- Constraints
    CONSTRAINT chk_user_relationships_different_users CHECK (user1_id != user2_id),
    CONSTRAINT chk_user_relationships_date_range CHECK (end_date IS NULL OR end_date >= start_date),
    CONSTRAINT chk_user_relationships_status CHECK (status IN ('ACTIVE', 'ENDED', 'SUSPENDED', 'PENDING'))
);

-- Create indexes for user_relationships
CREATE UNIQUE INDEX idx_user_relationships_id ON user_relationships(id);
CREATE INDEX idx_user_relationships_user1_id ON user_relationships(user1_id);
CREATE INDEX idx_user_relationships_user2_id ON user_relationships(user2_id);
CREATE INDEX idx_user_relationships_relationship_type_id ON user_relationships(relationship_type_id);
CREATE INDEX idx_user_relationships_context_id ON user_relationships(context_id);
CREATE INDEX idx_user_relationships_status ON user_relationships(status);
CREATE INDEX idx_user_relationships_start_date ON user_relationships(start_date);
CREATE INDEX idx_user_relationships_end_date ON user_relationships(end_date);

-- Composite indexes for common queries
CREATE INDEX idx_user_relationships_user1_status ON user_relationships(user1_id, status);
CREATE INDEX idx_user_relationships_user2_status ON user_relationships(user2_id, status);
CREATE INDEX idx_user_relationships_type_status ON user_relationships(relationship_type_id, status);
CREATE INDEX idx_user_relationships_users_type ON user_relationships(user1_id, user2_id, relationship_type_id);

-- Add comments
COMMENT ON TABLE user_relationships IS 'Stores actual relationships between users with their properties and status';
COMMENT ON COLUMN user_relationships.user1_id IS 'ID of the first user in the relationship';
COMMENT ON COLUMN user_relationships.user2_id IS 'ID of the second user in the relationship';
COMMENT ON COLUMN user_relationships.relationship_type_id IS 'Reference to the relationship type';
COMMENT ON COLUMN user_relationships.context_id IS 'Optional context (e.g., family circle ID)';
COMMENT ON COLUMN user_relationships.start_date IS 'When the relationship began';
COMMENT ON COLUMN user_relationships.end_date IS 'When the relationship ended (NULL for active relationships)';
COMMENT ON COLUMN user_relationships.status IS 'Current status of the relationship';
COMMENT ON COLUMN user_relationships.metadata IS 'JSON field for additional relationship properties';
