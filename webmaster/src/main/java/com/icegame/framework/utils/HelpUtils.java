package com.icegame.framework.utils;

import com.icegame.sysmanage.entity.HelpSys;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
 
public class HelpUtils {

    public static List<HelpSys> sort(List<HelpSys> helpSysList){
        Collections.sort(helpSysList, new Comparator<HelpSys>(){

            @Override
            public int compare(HelpSys o1, HelpSys o2) {

                if (o1.getZan() > o2.getZan()){
                    return -1;
                }

                if (o1.getZan() < o2.getZan()){
                    return 1;
                }

                return 0;
            }

        });
        return helpSysList;
    }

}