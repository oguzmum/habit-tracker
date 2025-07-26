-- There were issues when I first executed some sql scripts with dummy data
-- In the background, the system tried to insert database entries with specific IDs.
-- Due to earlier errors, these IDs didn't actually exist when used as foreign keys,
-- which caused foreign key constraint violations.
-- And as the ID is defined as Autoincrement, I couldn't set the same ID again

-- To fix this, I reset the tables and their auto-increment counters using:
-- TRUNCATE TABLE habit_streaks, habit_entries, habits, categories, goals RESTART IDENTITY CASCADE;


-- don't set the ID manually anymore, instead autoincrement sets it automatically
INSERT INTO goals (name, description) VALUES
  ('Gesünder leben', 'Ziele rund um Ernährung, Bewegung, Schlaf'),
  ('Produktivität steigern', 'Ziele rund um Fokus und Effizienz');


INSERT INTO categories (name, description) VALUES
  ('Sport', 'Bewegung & Fitness'),
  ('Ernährung', 'Essverhalten & Kochen'),
  ('Arbeit', 'Berufliche Routinen');


-- additionally to the TRUNCATE, i select the ID dynamically now with a sql query instead of hardcoding it :D
INSERT INTO habits (name, description, priority, start_date, frequency_type, frequency_value, goal_id, category_id)
VALUES
  (
    'Joggen gehen', '3x pro Woche mindestens 20 Minuten', 1, current_date, 'WEEKLY', 3,
    (SELECT id FROM goals WHERE name = 'Gesünder leben'),
    (SELECT id FROM categories WHERE name = 'Sport')
  ),
  (
    'Täglich Wasser trinken', 'Mind. 2 Liter Wasser am Tag', 2, current_date, 'DAILY', 1,
    (SELECT id FROM goals WHERE name = 'Gesünder leben'),
    (SELECT id FROM categories WHERE name = 'Ernährung')
  ),
  (
    'Pomodoro Sessions', '3 konzentrierte Fokusblöcke am Tag', 1, current_date, 'DAILY', 3,
    (SELECT id FROM goals WHERE name = 'Produktivität steigern'),
    (SELECT id FROM categories WHERE name = 'Arbeit')
  );

INSERT INTO habit_entries (habit_id, date) VALUES
  ((SELECT id FROM habits WHERE name = 'Joggen gehen'), current_date - INTERVAL '1 day'),
  ((SELECT id FROM habits WHERE name = 'Joggen gehen'), current_date),
  ((SELECT id FROM habits WHERE name = 'Täglich Wasser trinken'), current_date),
  ((SELECT id FROM habits WHERE name = 'Pomodoro Sessions'), current_date - INTERVAL '2 days');

INSERT INTO habit_streaks (habit_id, start_date, end_date) VALUES
  ((SELECT id FROM habits WHERE name = 'Joggen gehen'), current_date - INTERVAL '7 days', current_date - INTERVAL '3 days'),
  ((SELECT id FROM habits WHERE name = 'Täglich Wasser trinken'), current_date - INTERVAL '4 days', current_date);
