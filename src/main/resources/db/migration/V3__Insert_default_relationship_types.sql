-- Insert default relationship types
-- Family relationships
INSERT INTO relationship_types (name, category, bidirectional, reverse_type_id, metadata) VALUES
('Father', 'FAMILY', false, NULL, '{"description": "Male parent", "gender": "male", "generation": "parent"}'),
('Mother', 'FAMILY', false, NULL, '{"description": "Female parent", "gender": "female", "generation": "parent"}'),
('Son', 'FAMILY', false, NULL, '{"description": "Male child", "gender": "male", "generation": "child"}'),
('Daughter', 'FAMILY', false, NULL, '{"description": "Female child", "gender": "female", "generation": "child"}'),
('Brother', 'FAMILY', true, NULL, '{"description": "Male sibling", "gender": "male", "generation": "sibling"}'),
('Sister', 'FAMILY', true, NULL, '{"description": "Female sibling", "gender": "female", "generation": "sibling"}'),
('Husband', 'FAMILY', false, NULL, '{"description": "Male spouse", "gender": "male", "generation": "spouse"}'),
('Wife', 'FAMILY', false, NULL, '{"description": "Female spouse", "gender": "female", "generation": "spouse"}'),
('Grandfather', 'FAMILY', false, NULL, '{"description": "Father of parent", "gender": "male", "generation": "grandparent"}'),
('Grandmother', 'FAMILY', false, NULL, '{"description": "Mother of parent", "gender": "female", "generation": "grandparent"}'),
('Grandson', 'FAMILY', false, NULL, '{"description": "Son of child", "gender": "male", "generation": "grandchild"}'),
('Granddaughter', 'FAMILY', false, NULL, '{"description": "Daughter of child", "gender": "female", "generation": "grandchild"}'),
('Uncle', 'FAMILY', false, NULL, '{"description": "Brother of parent", "gender": "male", "generation": "uncle_aunt"}'),
('Aunt', 'FAMILY', false, NULL, '{"description": "Sister of parent", "gender": "female", "generation": "uncle_aunt"}'),
('Nephew', 'FAMILY', false, NULL, '{"description": "Son of sibling", "gender": "male", "generation": "nephew_niece"}'),
('Niece', 'FAMILY', false, NULL, '{"description": "Daughter of sibling", "gender": "female", "generation": "nephew_niece"}'),
('Cousin', 'FAMILY', true, NULL, '{"description": "Child of uncle/aunt", "generation": "cousin"}'),
('Father-in-law', 'FAMILY', false, NULL, '{"description": "Father of spouse", "gender": "male", "generation": "in_law"}'),
('Mother-in-law', 'FAMILY', false, NULL, '{"description": "Mother of spouse", "gender": "female", "generation": "in_law"}'),
('Son-in-law', 'FAMILY', false, NULL, '{"description": "Husband of child", "gender": "male", "generation": "in_law"}'),
('Daughter-in-law', 'FAMILY', false, NULL, '{"description": "Wife of child", "gender": "female", "generation": "in_law"}'),
('Brother-in-law', 'FAMILY', false, NULL, '{"description": "Brother of spouse or husband of sibling", "gender": "male", "generation": "in_law"}'),
('Sister-in-law', 'FAMILY', false, NULL, '{"description": "Sister of spouse or wife of sibling", "gender": "female", "generation": "in_law"}'),
('Stepfather', 'FAMILY', false, NULL, '{"description": "Husband of mother (not biological father)", "gender": "male", "generation": "step_parent"}'),
('Stepmother', 'FAMILY', false, NULL, '{"description": "Wife of father (not biological mother)", "gender": "female", "generation": "step_parent"}'),
('Stepson', 'FAMILY', false, NULL, '{"description": "Son of spouse (not biological child)", "gender": "male", "generation": "step_child"}'),
('Stepdaughter', 'FAMILY', false, NULL, '{"description": "Daughter of spouse (not biological child)", "gender": "female", "generation": "step_child"}'),
('Half-brother', 'FAMILY', true, NULL, '{"description": "Brother sharing one parent", "gender": "male", "generation": "half_sibling"}'),
('Half-sister', 'FAMILY', true, NULL, '{"description": "Sister sharing one parent", "gender": "female", "generation": "half_sibling"}');

-- Social relationships
INSERT INTO relationship_types (name, category, bidirectional, reverse_type_id, metadata) VALUES
('Friend', 'SOCIAL', true, NULL, '{"description": "Close personal relationship", "intimacy": "high"}'),
('Best Friend', 'SOCIAL', true, NULL, '{"description": "Closest personal relationship", "intimacy": "very_high"}'),
('Acquaintance', 'SOCIAL', true, NULL, '{"description": "Casual social relationship", "intimacy": "low"}'),
('Neighbor', 'SOCIAL', true, NULL, '{"description": "Person living nearby", "proximity": "close"}'),
('Colleague', 'SOCIAL', true, NULL, '{"description": "Person working together", "context": "work"}'),
('Mentor', 'SOCIAL', false, NULL, '{"description": "Person providing guidance", "role": "teacher"}'),
('Mentee', 'SOCIAL', false, NULL, '{"description": "Person receiving guidance", "role": "student"}'),
('Roommate', 'SOCIAL', true, NULL, '{"description": "Person sharing living space", "proximity": "very_close"}');

-- Professional relationships
INSERT INTO relationship_types (name, category, bidirectional, reverse_type_id, metadata) VALUES
('Boss', 'PROFESSIONAL', false, NULL, '{"description": "Supervisor or manager", "hierarchy": "superior"}'),
('Employee', 'PROFESSIONAL', false, NULL, '{"description": "Person being supervised", "hierarchy": "subordinate"}'),
('Coworker', 'PROFESSIONAL', true, NULL, '{"description": "Person working at same organization", "hierarchy": "peer"}'),
('Client', 'PROFESSIONAL', false, NULL, '{"description": "Person receiving services", "role": "customer"}'),
('Service Provider', 'PROFESSIONAL', false, NULL, '{"description": "Person providing services", "role": "provider"}'),
('Business Partner', 'PROFESSIONAL', true, NULL, '{"description": "Person in business relationship", "role": "partner"}'),
('Teacher', 'PROFESSIONAL', false, NULL, '{"description": "Person providing education", "role": "educator"}'),
('Student', 'PROFESSIONAL', false, NULL, '{"description": "Person receiving education", "role": "learner"}'),
('Doctor', 'PROFESSIONAL', false, NULL, '{"description": "Medical professional", "role": "healthcare_provider"}'),
('Patient', 'PROFESSIONAL', false, NULL, '{"description": "Person receiving medical care", "role": "healthcare_recipient"}');

-- Update reverse relationships for family types
UPDATE relationship_types SET reverse_type_id = (SELECT id FROM relationship_types WHERE name = 'Son') WHERE name = 'Father';
UPDATE relationship_types SET reverse_type_id = (SELECT id FROM relationship_types WHERE name = 'Daughter') WHERE name = 'Mother';
UPDATE relationship_types SET reverse_type_id = (SELECT id FROM relationship_types WHERE name = 'Father') WHERE name = 'Son';
UPDATE relationship_types SET reverse_type_id = (SELECT id FROM relationship_types WHERE name = 'Mother') WHERE name = 'Daughter';
UPDATE relationship_types SET reverse_type_id = (SELECT id FROM relationship_types WHERE name = 'Husband') WHERE name = 'Wife';
UPDATE relationship_types SET reverse_type_id = (SELECT id FROM relationship_types WHERE name = 'Wife') WHERE name = 'Husband';
UPDATE relationship_types SET reverse_type_id = (SELECT id FROM relationship_types WHERE name = 'Grandson') WHERE name = 'Grandfather';
UPDATE relationship_types SET reverse_type_id = (SELECT id FROM relationship_types WHERE name = 'Granddaughter') WHERE name = 'Grandmother';
UPDATE relationship_types SET reverse_type_id = (SELECT id FROM relationship_types WHERE name = 'Grandfather') WHERE name = 'Grandson';
UPDATE relationship_types SET reverse_type_id = (SELECT id FROM relationship_types WHERE name = 'Grandmother') WHERE name = 'Granddaughter';
UPDATE relationship_types SET reverse_type_id = (SELECT id FROM relationship_types WHERE name = 'Nephew') WHERE name = 'Uncle';
UPDATE relationship_types SET reverse_type_id = (SELECT id FROM relationship_types WHERE name = 'Niece') WHERE name = 'Aunt';
UPDATE relationship_types SET reverse_type_id = (SELECT id FROM relationship_types WHERE name = 'Uncle') WHERE name = 'Nephew';
UPDATE relationship_types SET reverse_type_id = (SELECT id FROM relationship_types WHERE name = 'Aunt') WHERE name = 'Niece';
UPDATE relationship_types SET reverse_type_id = (SELECT id FROM relationship_types WHERE name = 'Son-in-law') WHERE name = 'Father-in-law';
UPDATE relationship_types SET reverse_type_id = (SELECT id FROM relationship_types WHERE name = 'Daughter-in-law') WHERE name = 'Mother-in-law';
UPDATE relationship_types SET reverse_type_id = (SELECT id FROM relationship_types WHERE name = 'Father-in-law') WHERE name = 'Son-in-law';
UPDATE relationship_types SET reverse_type_id = (SELECT id FROM relationship_types WHERE name = 'Mother-in-law') WHERE name = 'Daughter-in-law';
UPDATE relationship_types SET reverse_type_id = (SELECT id FROM relationship_types WHERE name = 'Stepson') WHERE name = 'Stepfather';
UPDATE relationship_types SET reverse_type_id = (SELECT id FROM relationship_types WHERE name = 'Stepdaughter') WHERE name = 'Stepmother';
UPDATE relationship_types SET reverse_type_id = (SELECT id FROM relationship_types WHERE name = 'Stepfather') WHERE name = 'Stepson';
UPDATE relationship_types SET reverse_type_id = (SELECT id FROM relationship_types WHERE name = 'Stepmother') WHERE name = 'Stepdaughter';

-- Update reverse relationships for professional types
UPDATE relationship_types SET reverse_type_id = (SELECT id FROM relationship_types WHERE name = 'Employee') WHERE name = 'Boss';
UPDATE relationship_types SET reverse_type_id = (SELECT id FROM relationship_types WHERE name = 'Boss') WHERE name = 'Employee';
UPDATE relationship_types SET reverse_type_id = (SELECT id FROM relationship_types WHERE name = 'Service Provider') WHERE name = 'Client';
UPDATE relationship_types SET reverse_type_id = (SELECT id FROM relationship_types WHERE name = 'Client') WHERE name = 'Service Provider';
UPDATE relationship_types SET reverse_type_id = (SELECT id FROM relationship_types WHERE name = 'Student') WHERE name = 'Teacher';
UPDATE relationship_types SET reverse_type_id = (SELECT id FROM relationship_types WHERE name = 'Teacher') WHERE name = 'Student';
UPDATE relationship_types SET reverse_type_id = (SELECT id FROM relationship_types WHERE name = 'Patient') WHERE name = 'Doctor';
UPDATE relationship_types SET reverse_type_id = (SELECT id FROM relationship_types WHERE name = 'Doctor') WHERE name = 'Patient';
UPDATE relationship_types SET reverse_type_id = (SELECT id FROM relationship_types WHERE name = 'Mentee') WHERE name = 'Mentor';
UPDATE relationship_types SET reverse_type_id = (SELECT id FROM relationship_types WHERE name = 'Mentor') WHERE name = 'Mentee';
