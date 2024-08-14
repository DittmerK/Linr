package com.dittmer.linr;

import java.util.Comparator;

public class SortLineNotes implements Comparator<LineNote>
{
    @Override
    public int compare(LineNote o1, LineNote o2) 
    {
        return o1.page - o2.page;
    }
}