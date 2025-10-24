CREATE TABLE IF NOT EXISTS auth.user_authorities (
    user_id VARCHAR(36) NOT NULL,
    authority_id BIGINT NOT NULL,
    PRIMARY KEY (user_id, authority_id),
    FOREIGN KEY (user_id) REFERENCES auth.users(id),
    FOREIGN KEY (authority_id) REFERENCES auth.authorities(id)
);