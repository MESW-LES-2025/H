-- Add OAuth2 authentication fields to users table

-- Make password nullable for OAuth users
ALTER TABLE lernia.users 
  ALTER COLUMN password DROP NOT NULL;

-- Add provider and provider_id columns
ALTER TABLE lernia.users 
  ADD COLUMN provider VARCHAR(20) DEFAULT 'LOCAL',
  ADD COLUMN provider_id VARCHAR(255);

-- Create index for faster OAuth lookups
CREATE INDEX idx_users_provider_providerid 
  ON lernia.users(provider, provider_id);

-- Update existing users to have LOCAL provider
UPDATE lernia.users 
SET provider = 'LOCAL' 
WHERE provider IS NULL;
