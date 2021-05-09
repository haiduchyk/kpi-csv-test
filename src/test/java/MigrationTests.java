import com.opencsv.exceptions.CsvException;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.opencsv.CSVReader;


import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MigrationTests
{
    private final String basePath = "C:\\Users\\haidu\\Downloads\\";

    private static Connection connection;

    @BeforeClass
    public static void connect() throws SQLException
    {
        connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/postgres", "postgres", "postgres");
    }

    @AfterClass
    public static void disconnect() throws SQLException
    {
        connection.close();
    }

    @Test
    public void orderByPrice() throws SQLException, IOException, CsvException
    {
        ResultSet result = executeQuery("select * from \"cars\" order by \"price\" desc");
        CsvParser stream = new CsvParser(new FileOutputStream(basePath + "result1.csv"));
        stream.extractData(result);
        var isEqual = IsEqual("expected1.csv", "result1.csv", "log1.txt");
        Assert.assertTrue(isEqual);
    }

    @Test
    public void groupByTitle() throws SQLException, IOException, CsvException
    {
        ResultSet result = executeQuery("select count(*), \"ContactTitle\" from customers group by \"ContactTitle\"");
        CsvParser stream = new CsvParser(new FileOutputStream(basePath + "result2.csv"));
        stream.extractData(result);
        IsEqual("expected2.csv", "result2.csv", "log2.txt");
    }

    private ResultSet executeQuery(String query) throws SQLException
    {
        Statement statement = connection.createStatement();
        return statement.executeQuery(query);
    }

    private boolean IsEqual(String expected, String result, String output) throws IOException, CsvException
    {
        List<String[]> expectedLines = ReadAllLines(basePath + expected);
        List<String[]> resultLines = ReadAllLines(basePath + result);
        List<RowDifference> differences = GetDifferences(expectedLines, resultLines);
        Write(basePath + output, expectedLines.size(), resultLines.size(), differences);
        return differences.size() == 0;
    }

    public void Write(String path, int expectedSize, int resultSize, List<RowDifference> rows)
    {
        List<String> diffs = new ArrayList<>();
        diffs.add("Expected size: " + expectedSize);
        diffs.add("Result size: " + resultSize);
        if (rows.size() == 0)
        {
            diffs.add("No differences");
        }
        for (RowDifference row : rows)
        {
            diffs.add(row.toString());
        }
        try {
            Files.write(Paths.get(path), diffs);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private List<String[]> ReadAllLines(String path) throws IOException, CsvException
    {
        return new CSVReader(new FileReader(path)).readAll();
    }

    private List<RowDifference> GetDifferences(List<String[]> expected, List<String[]> result)
    {
        List<RowDifference> rows = new ArrayList<>();
        for (int row = 0; row < result.size() && row < expected.size(); row++)
        {
            List<Difference> differences = new ArrayList<>();
            String[] resultRows = result.get(row);
            String[] expectedRows = expected.get(row);
            for (int col = 0; col < resultRows.length; col++)
            {
                if (!expectedRows[col].equals(resultRows[col]))
                {
                    differences.add(new Difference(expectedRows[col], resultRows[col] ,col));
                }
            }
            if (differences.size() > 0)
            {
                rows.add(new RowDifference(differences, row + 1));
            }
        }
        return rows;
    }
}
