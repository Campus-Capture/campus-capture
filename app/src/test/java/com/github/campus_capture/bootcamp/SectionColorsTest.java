package com.github.campus_capture.bootcamp;

import static com.github.campus_capture.bootcamp.authentication.Section.*;

import static org.junit.Assert.assertEquals;

import android.content.Context;

import androidx.test.platform.app.InstrumentationRegistry;

import com.github.campus_capture.bootcamp.map.SectionColors;

import org.junit.Test;

public class SectionColorsTest {

    @Test
    public void testColorsAreCorrect()
    {
        Context c = InstrumentationRegistry.getInstrumentation().getContext();

        assertEquals(c.getColor(R.color.ar_color), SectionColors.getColor(AR, c));
        assertEquals(c.getColor(R.color.gc_color), SectionColors.getColor(GC, c));
        assertEquals(c.getColor(R.color.sie_color), SectionColors.getColor(SIE, c));
        assertEquals(c.getColor(R.color.in_color), SectionColors.getColor(IN, c));
        assertEquals(c.getColor(R.color.sc_color), SectionColors.getColor(SC, c));
        assertEquals(c.getColor(R.color.cgc_color), SectionColors.getColor(CGC, c));
        assertEquals(c.getColor(R.color.ma_color), SectionColors.getColor(MA, c));
        assertEquals(c.getColor(R.color.ph_color), SectionColors.getColor(PH, c));
        assertEquals(c.getColor(R.color.el_color), SectionColors.getColor(EL, c));
        assertEquals(c.getColor(R.color.sv_color), SectionColors.getColor(SV, c));
        assertEquals(c.getColor(R.color.mx_color), SectionColors.getColor(MX, c));
        assertEquals(c.getColor(R.color.gm_color), SectionColors.getColor(GM, c));
        assertEquals(c.getColor(R.color.mt_color), SectionColors.getColor(MT, c));
        assertEquals(c.getColor(R.color.none_color), SectionColors.getColor(NONE, c));
    }

}
