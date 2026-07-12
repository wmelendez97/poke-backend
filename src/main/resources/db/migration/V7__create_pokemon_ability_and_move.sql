-- Catalog of Pokemon abilities synchronized from PokeAPI.
CREATE TABLE ability (
    id BIGINT PRIMARY KEY,
    name VARCHAR(150) NOT NULL UNIQUE,
    raw_json TEXT
);

-- Relation between Pokemon and abilities, including hidden flag and slot.
CREATE TABLE pokemon_ability (
    pokemon_id BIGINT NOT NULL,
    ability_id BIGINT NOT NULL,
    is_hidden BOOLEAN,
    slot INTEGER,
    PRIMARY KEY (pokemon_id, ability_id),
    CONSTRAINT fk_pokemon_ability_pokemon FOREIGN KEY (pokemon_id) REFERENCES pokemon (pokemon_id),
    CONSTRAINT fk_pokemon_ability_ability FOREIGN KEY (ability_id) REFERENCES ability (id)
);

-- Catalog of Pokemon moves synchronized from PokeAPI.
CREATE TABLE move (
    id BIGINT PRIMARY KEY,
    name VARCHAR(150) NOT NULL UNIQUE,
    raw_json TEXT
);

-- Relation between Pokemon and moves.
CREATE TABLE pokemon_move (
    pokemon_id BIGINT NOT NULL,
    move_id BIGINT NOT NULL,
    PRIMARY KEY (pokemon_id, move_id),
    CONSTRAINT fk_pokemon_move_pokemon FOREIGN KEY (pokemon_id) REFERENCES pokemon (pokemon_id),
    CONSTRAINT fk_pokemon_move_move FOREIGN KEY (move_id) REFERENCES move (id)
);
