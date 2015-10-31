package buffer_manager;

import common.Column;
import common.conditions.Conditions;
import common.table_classes.MetaPage;
import common.table_classes.Page;
import common.table_classes.Table;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;
import common.xml.XMLBuilder;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;


/**
 * Created by semionn on 09.10.15.
 */
public class HeapBufferManager extends AbstractBufferManager {

    List<Page> fullPages;
    List<Page> incompletePages;
    XMLBuilder sysTable;

    public HeapBufferManager(Integer maxPagesCount) {
        super(maxPagesCount);
        // Absolute path for root data base
        Path filePath = Paths.get("data//root_db.ndb");
        sysTable = new XMLBuilder(filePath.toAbsolutePath().toString());
    }

    @Override
    public void createTable(String directory, Table table) {
        String tableName = table.getName();
        Path pathToTable = Paths.get(directory + table.getFileName());
        if (!sysTable.isExist(tableName)) {
            // Creating new table
            File tableFile = createTableFile(directory, pathToTable);
            defaultTableFilling(tableFile, table);
            // Modify Sys Table
            sysTable.addRecord(tableName, pathToTable.toString());
            sysTable.storeXMLDocument();
        } else {
            System.out.println("Table name duplication!");
            // Own exception should be thrown
        }
    }

    @Override
    public void insert(Table table, List<Column> columns, Conditions assignments) {
        // TODO: find table name through XML sys.table
        throw new NotImplementedException();
    }

    @Override
    public List<Page> getPages(Table table, Conditions conditions) {
        // TODO: pages which
        throw new NotImplementedException();
    }

    /*
        Creates new File for table "fileName" in ../data/
     */
    private File createTableFile(String directory, Path filePath) {
        File tableFile = new File(filePath.toAbsolutePath().toString());

        // Preventing table re-creation
        try {
            tableFile.createNewFile();
        } catch (IOException alreadyExistException) {
            System.out.println("Table with name = " + filePath.normalize().toString() + " already exist!");
            alreadyExistException.printStackTrace();
        }

        return tableFile;
    }

    /*
        Creates serializable page with meta-info and writs int to tableFile
     */
    private void defaultTableFilling(File tableFile, Table table) {
        try {
            FileOutputStream fileOutput = new FileOutputStream(tableFile);
            ObjectOutputStream objectOutput = new ObjectOutputStream(fileOutput);
            // Using Serializable MataPage representation
            MetaPage defaultPage = new MetaPage(table.getColumns());
            objectOutput.writeObject(defaultPage);
            objectOutput.flush();
            objectOutput.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
