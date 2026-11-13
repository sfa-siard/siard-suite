CREATE SCHEMA "SiardGui44";

CREATE TABLE "SiardGui44"."Documents"
(
    "DocumentID"     INT          NOT NULL,
    "DocumentNumber" VARCHAR(50)  NOT NULL,
    "Title"          VARCHAR(200) NULL,
    CONSTRAINT "PK_Documents" PRIMARY KEY ("DocumentID"),
    CONSTRAINT "UQ_Documents_DocumentNumber" UNIQUE ("DocumentNumber")
);

INSERT INTO "SiardGui44"."Documents" ("DocumentID", "DocumentNumber", "Title")
VALUES (1, 'DOC-001', 'First document'),
       (2, 'DOC-002', 'Second document'),
       (3, 'DOC-003', NULL);
