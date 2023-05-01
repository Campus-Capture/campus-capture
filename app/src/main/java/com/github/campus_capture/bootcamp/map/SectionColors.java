package com.github.campus_capture.bootcamp.map;

import android.content.Context;

import androidx.core.content.ContextCompat;

import com.github.campus_capture.bootcamp.R;
import com.github.campus_capture.bootcamp.authentication.Section;

public class SectionColors {

    private static final int alpha = 25;

    public static int getColor(Section s, Context context)
    {
        int id = -1;
        switch(s)
        {
            case AR:
                id = R.color.ar_color;
                break;

            case GC:
                id = R.color.gc_color;
                break;

            case SIE:
                id = R.color.sie_color;
                break;

            case IN:
                id = R.color.in_color;
                break;

            case SC:
                id = R.color.sc_color;
                break;

            case CGC:
                id = R.color.cgc_color;
                break;

            case MA:
                id = R.color.ma_color;
                break;

            case PH:
                id = R.color.ph_color;
                break;

            case EL:
                id = R.color.el_color;
                break;

            case SV:
                id = R.color.sv_color;
                break;

            case MX:
                id = R.color.mx_color;
                break;

            case GM:
                id = R.color.gm_color;
                break;

            case MT:
                id = R.color.mt_color;
                break;

            case NONE:
                id = R.color.none_color;
                break;
        }
        return ContextCompat.getColor(context, id);
    }

}
