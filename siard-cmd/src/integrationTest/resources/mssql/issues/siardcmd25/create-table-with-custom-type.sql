CREATE TYPE [dbo].[dtDay2] FROM [tinyint] NOT NULL;

CREATE TABLE [dbo].[testtable]
(
    [ID] [int] IDENTITY (1,1) NOT NULL,
    [DAY]     [dbo].[dtDay2]       NULL
);