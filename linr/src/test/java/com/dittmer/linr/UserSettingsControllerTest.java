package com.dittmer.linr;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class UserSettingsControllerTest 
{
    @Test
    public void testSwap()
    {
        assertEquals("{ACTOR}{SM}", new UserSettingsController().swapVariables("%1$s%2$s"));
        assertEquals("%1$s%2$s", new UserSettingsController().swapVariables("{ACTOR}{SM}"));
    }    
}
