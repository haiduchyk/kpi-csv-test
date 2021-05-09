import java.io.OutputStream;
import java.io.PrintWriter;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

public class CsvParser
{
    private static final char DELIMITER = ',';

    private final OutputStream os;

    public CsvParser(final OutputStream os)
    {
        this.os = os;
    }

    public void extractData(final ResultSet rs)
    {
        try (PrintWriter pw = new PrintWriter(os, true))
        {
            ResultSetMetaData rsmd = rs.getMetaData();
            int columnCount = rsmd.getColumnCount();
            writeHeader(rsmd, columnCount, pw);
            while (rs.next())
            {
                for (int i = 1; i <= columnCount; i++)
                {
                    Object value = rs.getObject(i);
                    pw.write(value == null ? "" : value.toString());
                    if (i != columnCount)
                    {
                        pw.append(DELIMITER);
                    }
                }
                pw.println();
            }
            pw.flush();
        }
        catch (final SQLException e)
        {
            throw new RuntimeException(e);
        }
    }

    private static void writeHeader(final ResultSetMetaData rsmd, final int columnCount, final PrintWriter pw) throws SQLException
    {
        for (int i = 1; i <= columnCount; i++)
        {
            pw.write(rsmd.getColumnName(i));
            if (i != columnCount)
            {
                pw.append(DELIMITER);
            }
        }
        pw.println();
    }
}