package gmutils.database.sqlitecommands;

/**
 * Created by Ahmed El-Sayed (Glory Maker)
 * Computer Engineer / 2012
 * Android/iOS Developer with (Java/Kotlin, Swift)
 * Have experience with:
 * - (C/C++, C#) languages
 * - .NET environment
 * - AVR Microcontrollers
 * a.elsayedabdo@gmail.com
 * +201022663988
 */
public enum SQLKeyword {

    //Adds a column in an existing table
    ADD,

    //Adds a constraint after a table is already created
    ADD_CONSTRAINT,

    //Adds, deletes, or modifies columns in a table, or changes the data type of a column in a table
    ALTER,

    //Changes the data type of a column in a table
    ALTER_COLUMN,

    //Adds, deletes, or modifies columns in a table
    ALTER_TABLE,

    //Returns true if all of the subquery values meet the condition
    ALL,

    //Only includes rows where both conditions is true
    AND,

    //Returns true if any of the subquery values meet the condition
    ANY,

    //Renames a column or table with an alias
    AS,

    //Sorts the result set in ascending order
    ASC,

    //Creates a back up of an existing database
    BACKUP_DATABASE,

    //Selects values within a given range
    BETWEEN,

    //	Creates different outputs based on conditions
    CASE,

    //	A constraint that limits the value that can be placed in a column
    CHECK,

    //	Changes the data type of a column or deletes a column in a table
    COLUMN,

    //	Adds or deletes a constraint
    CONSTRAINT,

    //	Creates a database, index, view, table, or procedure
    CREATE,

    //	Creates a new SQL database
    CREATE_DATABASE,

    //	Creates an index on a table (allows duplicate values)
    CREATE_INDEX,

    //	Updates a view
    CREATE_OR_REPLACE_VIEW,

    //	Creates a new table in the database
    CREATE_TABLE,

    //	Creates a stored procedure
    CREATE_PROCEDURE,

    //	Creates a unique index on a table (no duplicate values)
    CREATE_UNIQUE_INDEX,

    //Creates a view based on the result set of a SELECT statement
    CREATE_VIEW,

    //	Creates or deletes an SQL database
    DATABASE,

    //	A constraint that provides a default value for a column
    DEFAULT,

    // Deletes rows from a table
    DELETE,

    //	Sorts the result set in descending order
    DESC,

    //	Selects only distinct (different) values
    DISTINCT,

    //	Deletes a column, constraint, database, index, table, or view
    DROP,

    //	Deletes a column in a table
    DROP_COLUMN,

    //	Deletes a UNIQUE, PRIMARY KEY, FOREIGN KEY, or CHECK constraint
    DROP_CONSTRAINT,

    //	Deletes an existing SQL database
    DROP_DATABASE,

    //	Deletes a DEFAULT constraint
    DROP_DEFAULT,

    //	Deletes an index in a table
    DROP_INDEX,

    //	Deletes an existing table in the database
    DROP_TABLE,

    //	Deletes a view
    DROP_VIEW,

    //	Executes a stored procedure
    EXEC,

    //	Tests for the existence of any record in a subquery
    EXISTS,

    //	A constraint that is a key used to link two tables together
    FOREIGN_KEY,

    //	Specifies which table to select or delete data from
    FROM,

    //	Returns all rows when there is a match in either left table or right table
    FULL_OUTER_JOIN,

    //	Groups the result set (used with aggregate functions: COUNT, MAX, MIN, SUM, AVG)
    GROUP_BY,

    //	Used instead of WHERE with aggregate functions
    HAVING,

    //	Allows you to specify multiple values in a WHERE clause
    IN,

    //	Creates or deletes an index in a table
    INDEX,

    //	Returns rows that have matching values in both tables
    INNER_JOIN,

    //	Inserts new rows in a table
    INSERT_INTO,

    //	Copies data from one table into another table
    INSERT_INTO_SELECT,

    //	Tests for empty values
    IS_NULL,

    //	Tests for non-empty values
    IS_NOT_NULL,

    //	Joins tables
    JOIN,

    //	Returns all rows from the left table, and the matching rows from the right table
    LEFT_JOIN,

    //	Searches for a specified pattern in a column
    LIKE,

    //	Specifies the number of records to return in the result set
    LIMIT,

    //	Only includes rows where a condition is not true
    NOT,

    //	A constraint that enforces a column to not accept NULL values
    NOT_NULL,

    //	Includes rows where either condition is true
    OR,

    //	Sorts the result set in ascending or descending order
    ORDER_BY,

    //Returns all rows when there is a match in either left table or right table
    OUTER_JOIN,

    //	A constraint that uniquely identifies each record in a database table
    PRIMARY_KEY,

    //	A stored procedure
    PROCEDURE,

    //	Returns all rows from the right table, and the matching rows from the left table
    RIGHT_JOIN,

    //	Specifies the number of records to return in the result set
    ROWNUM,

    //	Selects data from a database
    SELECT,

    //	Selects only distinct (different) values
    SELECT_DISTINCT,

    //	Copies data from one table into a new table
    SELECT_INTO,

    // Specifies the number of records to return in the result set
    SELECT_TOP,

    //	Specifies which columns and values that should be updated in a table
    SET,

    //	Creates a table, or adds, deletes, or modifies columns in a table, or deletes a table or data inside a table
    TABLE,

    //	Specifies the number of records to return in the result set
    TOP,

    //	Deletes the data inside a table, but not the table itself
    TRUNCATE_TABLE,

    //	Combines the result set of two or more SELECT statements (only distinct values)
    UNION,

    //	Combines the result set of two or more SELECT statements (allows duplicate values)
    UNION_ALL,

    //	A constraint that ensures that all values in a column are unique
    UNIQUE,

    //	Updates existing rows in a table
    UPDATE,

    //	Specifies the values of an INSERT INTO statement
    VALUES,

    //	Creates, updates, or deletes a view
    VIEW,

    //	Filters a result set to include only records that fulfill a specified condition
    WHERE;


    public String getText() {
        return this.name().replace("_", " ");
    }
}
