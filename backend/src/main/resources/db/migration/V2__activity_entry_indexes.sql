-- At most one running activity (no finished_at) per workday, enforced by the database.
CREATE UNIQUE INDEX uq_activity_entries_one_open_per_workday
    ON activity_entries (work_day_id)
    WHERE finished_at IS NULL;

-- Speeds up loading the history of a workday (ordered by start time).
CREATE INDEX idx_activity_entries_work_day_started
    ON activity_entries (work_day_id, started_at);
