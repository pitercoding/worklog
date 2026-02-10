-- Programs
INSERT INTO programs (name, active)
SELECT 'Orion Platform', true
WHERE NOT EXISTS (SELECT 1 FROM programs WHERE name = 'Orion Platform');

INSERT INTO programs (name, active)
SELECT 'Nova Fintech', true
WHERE NOT EXISTS (SELECT 1 FROM programs WHERE name = 'Nova Fintech');

INSERT INTO programs (name, active)
SELECT 'Atlas DevTools', true
WHERE NOT EXISTS (SELECT 1 FROM programs WHERE name = 'Atlas DevTools');

-- Teams
INSERT INTO teams (name, active)
SELECT 'Core Backend', true
WHERE NOT EXISTS (SELECT 1 FROM teams WHERE name = 'Core Backend');

INSERT INTO teams (name, active)
SELECT 'Frontend Experience', true
WHERE NOT EXISTS (SELECT 1 FROM teams WHERE name = 'Frontend Experience');

INSERT INTO teams (name, active)
SELECT 'DevOps Platform', true
WHERE NOT EXISTS (SELECT 1 FROM teams WHERE name = 'DevOps Platform');

-- Languages
INSERT INTO languages (name, active)
SELECT 'Java', true
WHERE NOT EXISTS (SELECT 1 FROM languages WHERE name = 'Java');

INSERT INTO languages (name, active)
SELECT 'TypeScript', true
WHERE NOT EXISTS (SELECT 1 FROM languages WHERE name = 'TypeScript');

INSERT INTO languages (name, active)
SELECT 'Python', true
WHERE NOT EXISTS (SELECT 1 FROM languages WHERE name = 'Python');

INSERT INTO languages (name, active)
SELECT 'Kotlin', true
WHERE NOT EXISTS (SELECT 1 FROM languages WHERE name = 'Kotlin');

INSERT INTO languages (name, active)
SELECT 'Go', true
WHERE NOT EXISTS (SELECT 1 FROM languages WHERE name = 'Go');

-- Activities
INSERT INTO activities (name, active)
SELECT 'Development', true
WHERE NOT EXISTS (SELECT 1 FROM activities WHERE name = 'Development');

INSERT INTO activities (name, active)
SELECT 'Code Review', true
WHERE NOT EXISTS (SELECT 1 FROM activities WHERE name = 'Code Review');

INSERT INTO activities (name, active)
SELECT 'Testing', true
WHERE NOT EXISTS (SELECT 1 FROM activities WHERE name = 'Testing');

INSERT INTO activities (name, active)
SELECT 'Bug Fixing', true
WHERE NOT EXISTS (SELECT 1 FROM activities WHERE name = 'Bug Fixing');

INSERT INTO activities (name, active)
SELECT 'Meeting', true
WHERE NOT EXISTS (SELECT 1 FROM activities WHERE name = 'Meeting');

INSERT INTO activities (name, active)
SELECT 'Documentation', true
WHERE NOT EXISTS (SELECT 1 FROM activities WHERE name = 'Documentation');

-- Subactivities: Development
INSERT INTO subactivities (activity_id, name, active)
SELECT a.id, 'Feature Implementation', true
FROM activities a
WHERE a.name = 'Development'
  AND NOT EXISTS (
    SELECT 1 FROM subactivities s
    WHERE s.activity_id = a.id AND s.name = 'Feature Implementation'
  );

INSERT INTO subactivities (activity_id, name, active)
SELECT a.id, 'Refactoring', true
FROM activities a
WHERE a.name = 'Development'
  AND NOT EXISTS (
    SELECT 1 FROM subactivities s
    WHERE s.activity_id = a.id AND s.name = 'Refactoring'
  );

INSERT INTO subactivities (activity_id, name, active)
SELECT a.id, 'Spike Research', true
FROM activities a
WHERE a.name = 'Development'
  AND NOT EXISTS (
    SELECT 1 FROM subactivities s
    WHERE s.activity_id = a.id AND s.name = 'Spike Research'
  );

-- Subactivities: Code Review
INSERT INTO subactivities (activity_id, name, active)
SELECT a.id, 'PR Review', true
FROM activities a
WHERE a.name = 'Code Review'
  AND NOT EXISTS (
    SELECT 1 FROM subactivities s
    WHERE s.activity_id = a.id AND s.name = 'PR Review'
  );

INSERT INTO subactivities (activity_id, name, active)
SELECT a.id, 'Pair Review', true
FROM activities a
WHERE a.name = 'Code Review'
  AND NOT EXISTS (
    SELECT 1 FROM subactivities s
    WHERE s.activity_id = a.id AND s.name = 'Pair Review'
  );

INSERT INTO subactivities (activity_id, name, active)
SELECT a.id, 'Architecture Review', true
FROM activities a
WHERE a.name = 'Code Review'
  AND NOT EXISTS (
    SELECT 1 FROM subactivities s
    WHERE s.activity_id = a.id AND s.name = 'Architecture Review'
  );

-- Subactivities: Testing
INSERT INTO subactivities (activity_id, name, active)
SELECT a.id, 'Unit Tests', true
FROM activities a
WHERE a.name = 'Testing'
  AND NOT EXISTS (
    SELECT 1 FROM subactivities s
    WHERE s.activity_id = a.id AND s.name = 'Unit Tests'
  );

INSERT INTO subactivities (activity_id, name, active)
SELECT a.id, 'Integration Tests', true
FROM activities a
WHERE a.name = 'Testing'
  AND NOT EXISTS (
    SELECT 1 FROM subactivities s
    WHERE s.activity_id = a.id AND s.name = 'Integration Tests'
  );

INSERT INTO subactivities (activity_id, name, active)
SELECT a.id, 'E2E Tests', true
FROM activities a
WHERE a.name = 'Testing'
  AND NOT EXISTS (
    SELECT 1 FROM subactivities s
    WHERE s.activity_id = a.id AND s.name = 'E2E Tests'
  );

-- Subactivities: Bug Fixing
INSERT INTO subactivities (activity_id, name, active)
SELECT a.id, 'Triage', true
FROM activities a
WHERE a.name = 'Bug Fixing'
  AND NOT EXISTS (
    SELECT 1 FROM subactivities s
    WHERE s.activity_id = a.id AND s.name = 'Triage'
  );

INSERT INTO subactivities (activity_id, name, active)
SELECT a.id, 'Root Cause Analysis', true
FROM activities a
WHERE a.name = 'Bug Fixing'
  AND NOT EXISTS (
    SELECT 1 FROM subactivities s
    WHERE s.activity_id = a.id AND s.name = 'Root Cause Analysis'
  );

INSERT INTO subactivities (activity_id, name, active)
SELECT a.id, 'Patch', true
FROM activities a
WHERE a.name = 'Bug Fixing'
  AND NOT EXISTS (
    SELECT 1 FROM subactivities s
    WHERE s.activity_id = a.id AND s.name = 'Patch'
  );

-- Subactivities: Meeting
INSERT INTO subactivities (activity_id, name, active)
SELECT a.id, 'Daily', true
FROM activities a
WHERE a.name = 'Meeting'
  AND NOT EXISTS (
    SELECT 1 FROM subactivities s
    WHERE s.activity_id = a.id AND s.name = 'Daily'
  );

INSERT INTO subactivities (activity_id, name, active)
SELECT a.id, 'Planning', true
FROM activities a
WHERE a.name = 'Meeting'
  AND NOT EXISTS (
    SELECT 1 FROM subactivities s
    WHERE s.activity_id = a.id AND s.name = 'Planning'
  );

INSERT INTO subactivities (activity_id, name, active)
SELECT a.id, 'Retrospective', true
FROM activities a
WHERE a.name = 'Meeting'
  AND NOT EXISTS (
    SELECT 1 FROM subactivities s
    WHERE s.activity_id = a.id AND s.name = 'Retrospective'
  );

-- Subactivities: Documentation
INSERT INTO subactivities (activity_id, name, active)
SELECT a.id, 'API Docs', true
FROM activities a
WHERE a.name = 'Documentation'
  AND NOT EXISTS (
    SELECT 1 FROM subactivities s
    WHERE s.activity_id = a.id AND s.name = 'API Docs'
  );

INSERT INTO subactivities (activity_id, name, active)
SELECT a.id, 'Tech Spec', true
FROM activities a
WHERE a.name = 'Documentation'
  AND NOT EXISTS (
    SELECT 1 FROM subactivities s
    WHERE s.activity_id = a.id AND s.name = 'Tech Spec'
  );

INSERT INTO subactivities (activity_id, name, active)
SELECT a.id, 'User Guide', true
FROM activities a
WHERE a.name = 'Documentation'
  AND NOT EXISTS (
    SELECT 1 FROM subactivities s
    WHERE s.activity_id = a.id AND s.name = 'User Guide'
  );
