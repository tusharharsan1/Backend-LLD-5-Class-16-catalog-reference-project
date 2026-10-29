-- V3: two categories may not have the same name
ALTER TABLE category ADD CONSTRAINT uk_category_name UNIQUE (name);
