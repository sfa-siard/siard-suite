-- Schema creation
CREATE SCHEMA siardgui44;

-- Table creation
CREATE TABLE siardgui44.documents
(
    document_id     INT          NOT NULL,
    document_number VARCHAR(50)  NOT NULL,
    title           VARCHAR(200),
    CONSTRAINT pk_documents PRIMARY KEY (document_id),
    CONSTRAINT uq_documents_document_number UNIQUE (document_number)
);

-- Test data
INSERT INTO siardgui44.documents (document_id, document_number, title)
VALUES (1, 'DOC-001', 'First document'),
       (2, 'DOC-002', 'Second document'),
       (3, 'DOC-003', NULL);
