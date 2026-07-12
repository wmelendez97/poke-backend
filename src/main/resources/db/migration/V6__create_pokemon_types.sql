CREATE TABLE pokemon_type (
    id BIGINT PRIMARY KEY,
    name VARCHAR(150) NOT NULL UNIQUE
);

CREATE TABLE pokemon_type_rel (
    pokemon_id BIGINT NOT NULL,
    type_id BIGINT NOT NULL,
    PRIMARY KEY (pokemon_id, type_id),
    CONSTRAINT fk_pokemon_type_rel_pokemon FOREIGN KEY (pokemon_id) REFERENCES pokemon (pokemon_id),
    CONSTRAINT fk_pokemon_type_rel_type FOREIGN KEY (type_id) REFERENCES pokemon_type (id)
);
