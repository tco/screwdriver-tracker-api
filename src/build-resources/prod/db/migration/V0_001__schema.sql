CREATE TABLE tracker (
    id SERIAL PRIMARY KEY,
    trackerCode VARCHAR(200) NOT NULL UNIQUE,
);

CREATE TABLE event (
    id SERIAL PRIMARY KEY,
    tracker_id INTEGER NOT NULL,
    version VARCHAR(10) NOT NULL,
    submitTime TIMESTAMP NOT NULL,
    eventTimestamp TIMESTAMP NULL
);
ALTER TABLE event ADD CONSTRAINT fkEventTracker FOREIGN KEY (tracker_id) REFERENCES tracker (id);

CREATE TABLE eventParameter (
    id SERIAL PRIMARY KEY,
    event_id INTEGER NOT NULL,
    key VARCHAR(100) NOT NULL,
    value VARCHAR(256) NOT NULL
);
ALTER TABLE eventParameter ADD CONSTRAINT fkEventParameterEvent FOREIGN KEY (event_id) REFERENCES event (id);
