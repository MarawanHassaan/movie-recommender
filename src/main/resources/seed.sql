DELETE FROM movie_genre;
DELETE FROM ranking;
DELETE FROM genres;
DELETE FROM movies;
DELETE FROM users;

SELECT setval(pg_get_serial_sequence('genres', 'id'), 1, false);
SELECT setval(pg_get_serial_sequence('movies', 'id'), 1, false);
SELECT setval(pg_get_serial_sequence('users', 'id'), 1, false);

INSERT INTO users (username) VALUES
('Alice'),    -- 1
('Bob'),      -- 2
('Charlie');  -- 3


INSERT INTO movies (title) VALUES
('Toy Story'),                       -- 1
('Grumpier Old Men'),                -- 2
('Die Hard'),                        -- 3
('Star Wars: Return of the Jedi'),   -- 4
('The Lion King'),                   -- 5
('Pulp Fiction'),                    -- 6
('Forrest Gump'),                    -- 7
('The Matrix'),                      -- 8
('Goodfellas'),                      -- 9
('Jurassic Park');                   -- 10



INSERT INTO genres (name) VALUES
('Adventure'), -- 1
('Animation'), -- 2
('Action'),    -- 3
('Biography'), -- 4
('Children'),  -- 5
('Crime'),     -- 6
('Comedy'),    -- 7
('Drama'),     -- 8
('Fantasy'),   -- 9
('Musical'),   -- 10
('Romance'),   -- 11
('Sci-Fi'),    -- 12
('Thriller');  -- 13


INSERT INTO movie_genre (movie_id, genre_id) VALUES
-- Toy Story: Adventure|Animation|Children|Comedy|Fantasy
(1, 1), (1, 2), (1, 5), (1, 7), (1, 9),

-- Grumpier Old Men: Comedy|Romance
(2, 7), (2, 11),

-- Die Hard: Action|Thriller
(3, 3), (3, 13),

-- Star Wars: Return of the Jedi: Action|Adventure|Fantasy|Sci-Fi
(4, 3), (4, 1), (4, 9), (4, 12),

-- The Lion King: Adventure|Animation|Children|Drama|Musical
(5, 1), (5, 2), (5, 5), (5, 8), (5, 10),

-- Pulp Fiction: Crime|Drama|Thriller
(6, 6), (6, 8), (6, 13),

-- Forrest Gump: Comedy|Drama|Romance
(7, 7), (7, 8), (7, 11),

-- The Matrix: Action|Sci-Fi
(8, 3), (8, 12),

-- Goodfellas: Biography|Crime|Drama
(9, 4), (9, 6), (9, 8),

-- Jurassic Park: Adventure|Sci-Fi|Thriller
(10, 1), (10, 12), (10, 13);



INSERT INTO ranking (user_id, movie_id, rank1, rank2) VALUES
(1, 1, 4, 85),           -- Alice ranks Toy Story (both)
(1, 2, 5, NULL),         -- Alice ranks Grumpier Old Men (rank1)
(2, 1, NULL, 90),        -- Bob ranks Toy Story (rank2)
(2, 3, 3, NULL),         -- Bob ranks Die Hard (rank1)
(3, 4, NULL, 70),        -- Charlie ranks Star Wars (rank2)
(3, 2, 2, NULL);         -- Charlie ranks Grumpier Old Men (only rank1)
