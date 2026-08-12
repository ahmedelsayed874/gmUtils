package gmutils.database.sqlitecommands;

/**
 * Created by Ahmed El-Sayed (Glory Maker)
 * Computer Engineer / 2012
 * Android/iOS Developer (Java/Kotlin, Swift) also Flutter (Dart)
 * Have precedent experience with:
 * - (C/C++, C#) languages
 * - .NET environment
 * - Java swing
 * - AVR Microcontrollers
 * a.elsayedabdo@gmail.com
 * +201022663988
 */
public class AlterTable implements ICommand {
    private final String tableName;
    private CreateTable.Column column;
    private String alterAction;

    public AlterTable(String tableName) {
        if (tableName == null || tableName.length() == 0) {
            throw new RuntimeException("Table Name not allowed to be null or empty");

        } else {
            this.tableName = tableName;
        }
    }

    public void renameTable(String newName) {
        column = null;
        alterAction = "RENAME TO " + newName;
    }

    public void addColumn(String name, CreateTable.DataTypes dataType, CreateTable.Constraint[] constraints) {
        column = new CreateTable.Column(name, dataType, constraints);
        alterAction = "ADD";
    }

    public void dropColumn(String name) {
        column = null;
        alterAction = "DROP COLUMN " + name;
    }

    public void modifyColumn(String name, CreateTable.DataTypes dataType, CreateTable.Constraint[] constraints) {
        column = new CreateTable.Column(name, dataType, constraints);
        alterAction = "ALTER COLUMN";
    }

    public void renameColumn(String oldName, String newName) {
        column = null;
        alterAction = "RENAME COLUMN " + oldName + " to " + newName;
    }

    @Override
    public String getCode() {
        String columnCode = column == null ? "" : column.getCode();
        return "ALTER TABLE " + this.tableName + " " + alterAction + " " + columnCode + ";";
    }

    @Override
    public String toString() {
        return getCode();
    }
}
