CREATE TABLE [dbo].[testtable]
(
    [ID]          [int] IDENTITY (1,1) NOT NULL,
    [COLUMN1]     [int] NULL
    );

EXEC sys.sp_addextendedproperty @name=N'Caption',@value=N'Caption.',
@level0type=N'SCHEMA', @level0name=N'dbo', @level1type=N'TABLE', @level1name=N'testtable';

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'Description',
@level0type=N'SCHEMA', @level0name=N'dbo', @level1type=N'TABLE', @level1name=N'testtable';
