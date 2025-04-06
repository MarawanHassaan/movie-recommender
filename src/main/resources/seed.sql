DELETE FROM movie_genre;
DELETE FROM ranking;
DELETE FROM genres;
DELETE FROM movies;
DELETE FROM users;

SELECT setval(pg_get_serial_sequence('genres', 'id'), 1, false);
SELECT setval(pg_get_serial_sequence('movies', 'id'), 1, false);
SELECT setval(pg_get_serial_sequence('users', 'id'), 1, false);

INSERT INTO users (username) VALUES
('Alice Johnson'),
('Bob Smith'),
('Charlie Davis');


INSERT INTO movies (title) VALUES
('Inception'),
('The Matrix'),
('Interstellar'),
('The Dark Knight');


INSERT INTO genres (name) VALUES
('Sci-Fi'),
('Action'),
('Drama'),
('Thriller');


INSERT INTO movie_genre (movie_id, genre_id) VALUES
(1, 1), -- Inception -> Sci-Fi
(1, 4), -- Inception -> Thriller
(2, 1), -- The Matrix -> Sci-Fi
(2, 2), -- The Matrix -> Action
(3, 1), -- Interstellar -> Sci-Fi
(3, 3), -- Interstellar -> Drama
(4, 2), -- The Dark Knight -> Action
(4, 4); -- The Dark Knight -> Thriller



INSERT INTO ranking (user_id, movie_id, rank1, rank2) VALUES
(1, 1, 5, NULL), -- Alice ranks Inception (only rank1)
(1, 2, NULL, 85), -- Alice ranks The Matrix (only rank2 -> should be 5)
(2, 3, 4, NULL), -- Bob ranks Interstellar (only rank1)
(2, 4, NULL, 60), -- Bob ranks The Dark Knight (rank2 -> should be 4)
(3, 1, NULL, 30), -- Charlie ranks Inception (rank2 -> should be 2)
(3, 2, 3, NULL); -- Charlie ranks The Matrix (only rank1)
