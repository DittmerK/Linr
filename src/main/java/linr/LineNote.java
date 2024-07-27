package linr;

public class LineNote
{
    public String actor;
    public String scene;
    public int page;
    public String action;
    public String line;
    public String notes;
    public int occurences;
    public boolean fixed;

    public static String[] actions = {"Called Line", "Paraphrased", "Jumped/Missed Cue", "Pronounciation", "Order of Words", "Missed Word", "Added Word"};
    public static LineNote parse(String input)
    {
        try
        {
            LineNote ret = new LineNote();
            String[] values = input.split(",");
            ret.actor = values[0];
            ret.scene = values[1];
            ret.page = Integer.parseInt(values[2]);
            ret.action = values[3];
            ret.line = values[4];
            ret.notes = values[5];
            ret.occurences = Integer.parseInt(values[6]);
            ret.fixed = Boolean.parseBoolean(values[7]);
            return ret;
        } catch(Exception e){}
        return null;
    }
    public String toString()
    {
        return actor + "," + scene + "," + page + "," + action + "," + line + "," + notes + "," + occurences + "," + (fixed ? "TRUE" : "FALSE");
    }
}