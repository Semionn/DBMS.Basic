package buffer_manager;

import commands_runner.cursors.ICursor;
import commands_runner.cursors.SimpleCursor;
import common.Column;
import common.conditions.Conditions;
import common.table_classes.Table;
import org.antlr.v4.runtime.misc.Pair;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;
import common.xml.XMLBuilder;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by semionn on 09.10.15.
 */
public class HeapBufferManager extends AbstractBufferManager {

    private XMLBuilder sysTable;
    private LoadEngine loadEngine;

    public HeapBufferManager(Integer maxPagesCount) {
        super(maxPagesCount);
        // Absolute path for root data base
        sysTable = new XMLBuilder(DATA_ROOT_DB_FILE.toAbsolutePath().toString());
        loadEngine = new LoadEngine(maxPagesCount);
    }

    @Override
    public void createTable(String directory, Table table) {
        String tableName = table.getName();
        Path pathToTable = Paths.get(directory + table.getFileName());
        table.setFileName(pathToTable.toAbsolutePath().toString());
        if (!sysTable.isExist(tableName)) {
            // Creating new table
            loadEngine.switchToTable(table);
            loadEngine.writeMetaPage(table);
            // Modify Sys Table
            sysTable.addRecord(tableName, pathToTable.toString());
            sysTable.storeXMLDocument();

        } else {
            System.out.println("Table name duplication!");
            // Own exception should be thrown
        }

        System.out.println(table.getName());
    }

    @Override
    public Map<String, Table> loadTables() {
        Map<String, Table> result = new HashMap<>();
        List<Pair<String, String>> content = sysTable.loadContents();
        for (Pair<String, String> item : content) {
            Table table = new Table(item.a, item.b, null);
            loadEngine.switchToTable(table);
            loadEngine.readMetaPage(table);
            result.put(item.a, table);
        }
        return result;
    }

    @Override
    public void insert(Table table, List<Column> columns, Conditions assignments) {
        if (!sysTable.isExist(table.getName())) {
            // Creating new table
            loadEngine.switchToTable(table);
            //loadEngine.storeRecordInPage();

        } else {
            System.out.println("We want to insert pag in Table which do not exist");
            // Own exception should be thrown
        }

        // TODO: find table name through XML sys.table
        throw new NotImplementedException();
    }

    @Override
    public ICursor getCursor(Table table, Conditions conditions) {
        // Optimize this
        if (sysTable.isExist(table.getName())) {
            String tablePath = sysTable.getTablePath(table.getName());
            loadEngine.switchToTable(table);
            return new SimpleCursor(loadEngine, table);
        } else {
            System.out.println("Not such data base file!");
        }
        return null;
    }
}
