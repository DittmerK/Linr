package com.dittmer.linr.util;

import java.util.Comparator;

import com.dittmer.linr.objects.LineNote;

public class SortLineNotes implements Comparator<LineNote>
{
    @Override
    public int compare(LineNote o1, LineNote o2) 
    {
        return o1.page - o2.page;
    }
}