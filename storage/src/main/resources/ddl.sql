CREATE TABLE Span
(
  trace_id VARCHAR(32) NOT NULL,
  id VARCHAR(16) NOT NULL,
  service_name VARCHAR(255), -- The localEndpoint.serviceName field in json
  name VARCHAR(255),
  ts TIMESTAMP, -- Derived from the epoch micros timestamp in json
  duration BIGINT, -- The duration field in json, in microseconds
  is_error TINYINT NOT NULL, -- 1 when tags.error exists in json or 0 if not
  md5 VARBINARY(16) NOT NULL, -- MD5 of the json, used to prevent duplicate rows
  json VARCHAR NOT NULL, -- Potentially incomplete v2 json sent by instrumentation
  PRIMARY KEY (trace_id, id, md5)
);

-- Allows procedures to work on a trace as a unit
PARTITION TABLE Span ON COLUMN trace_id;

CREATE PROCEDURE StoreSpanJson PARTITION ON TABLE Span COLUMN trace_id PARAMETER 0 AS
  INSERT INTO Span (trace_id, id, service_name, name, ts, duration, is_error, md5, json)
    VALUES (?, ?, ?, ?, TO_TIMESTAMP(Micros, ?), ?, ?, ?, ?);

CREATE PROCEDURE GetSpanJson PARTITION ON TABLE Span COLUMN trace_id PARAMETER 0 AS
  SELECT json from Span where trace_id = ?;

CREATE PROCEDURE GetSpansJson AS
  SELECT json from Span where ts BETWEEN TO_TIMESTAMP(Millis, ?) AND TO_TIMESTAMP(Millis, ?);
