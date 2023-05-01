package com.github.campus_capture.bootcamp.map;

import static com.github.campus_capture.bootcamp.authentication.Section.*;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;

import androidx.core.content.ContextCompat;

import com.github.campus_capture.bootcamp.R;
import com.github.campus_capture.bootcamp.authentication.Section;

import java.util.HashMap;
import java.util.Map;

public class SectionColors {

    private static final int alpha = 25;
    @SuppressLint("StaticFieldLeak")
    private static Context context = null;

    private static final Map<Section, Integer> sectionColors = new HashMap<>();

    public static Map<Section, Integer> sectionColorMap()
    {
        if(context == null)
        {
            throw new IllegalStateException("Context was not initialized");
        }
        if(sectionColors.isEmpty())
        {
            sectionColors.put(AR, ContextCompat.getColor(context, R.color.ar_color));
            sectionColors.put(GC, ContextCompat.getColor(context, R.color.gc_color));
            sectionColors.put(SIE, ContextCompat.getColor(context, R.color.sie_color));
            sectionColors.put(IN, ContextCompat.getColor(context, R.color.in_color));
            sectionColors.put(SC, ContextCompat.getColor(context, R.color.sc_color));
            sectionColors.put(CGC, ContextCompat.getColor(context, R.color.cgc_color));
            sectionColors.put(MA, ContextCompat.getColor(context, R.color.ma_color));
            sectionColors.put(PH, ContextCompat.getColor(context, R.color.ph_color));
            sectionColors.put(EL, ContextCompat.getColor(context, R.color.el_color));
            sectionColors.put(SV, ContextCompat.getColor(context, R.color.sv_color));
            sectionColors.put(MX, ContextCompat.getColor(context, R.color.mx_color));
            sectionColors.put(GM, ContextCompat.getColor(context, R.color.gm_color));
            sectionColors.put(MT, ContextCompat.getColor(context, R.color.mt_color));
            sectionColors.put(NONE, ContextCompat.getColor(context, R.color.none_color));
        }
        return sectionColors;
    }

    public void setContext(Context context)
    {
        SectionColors.context = context;
    }

}
