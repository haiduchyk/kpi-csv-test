class Difference
{
    private final String expected;
    private final String got;
    private final int col;

    public Difference(String expected, String got, int col)
    {
        this.expected = expected;
        this.got = got;
        this.col = col;
    }

    @Override
    public String toString()
    {
        return  "expected='" + expected + '\'' +
                ", got='" + got + '\'' +
                ", col='" + col + '\'';
    }
}

