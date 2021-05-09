import java.util.List;

class RowDifference
{
    private final List<Difference> differences;
    private final int row;

    RowDifference(List<Difference> differences, int row)
    {
        this.differences = differences;
        this.row = row;
    }

    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        sb.append("Row ");
        sb.append(row);
        sb.append('\n');
        for (Difference difference : differences)
        {
            sb.append('\t');
            sb.append(difference.toString());
            sb.append('\n');
        }
        return sb.toString();
    }
}

