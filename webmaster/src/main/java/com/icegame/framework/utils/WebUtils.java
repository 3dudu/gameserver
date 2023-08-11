package com.icegame.framework.utils;

import java.util.List;

/**
 * 模板渲染
 * @author wuzhijian
 * @date 2019-05-23
 */
public class WebUtils {

    /**
     * 定义基本的组件
     * @param style
     * @param args
     * @return
     */
    public static String table(String style, String args){
        return "<table style=' " + style + " '>" + args + "</table>";
    }

    public static String thead(String style, String args){
        return "<thead style=' " + style + " '>" + args + "</thead>";
    }

    public static String tbody(String style, String args){
        return "<tbody style=' " + style + " '>" + args + "</tbody>";
    }

    public static String th(String style, String args){
        return "<th style=' " + style + " '>" + args + "</th>";
    }

    public static String td(String style, String args){
        return "<td style=' " + style + " '>" + args + "</td>";
    }

    public static String tr(String style, String args){
        return "<tr style=' " + style + " '>" + args + "</tr>";
    }

    /**
     * 表头渲染
     * @return
     */
    public static String drawThead(){
        StringBuffer context = new StringBuffer();
        context.append(th("border-bottom: 1px solid #ddd;","Slave节点"));
        context.append(th("border-left: 1px solid #ddd;border-bottom: 1px solid #ddd;","已开服数"));
        context.append(th("border-left: 1px solid #ddd;border-bottom: 1px solid #ddd;","预备服数"));
        context.append(th("border-left: 1px solid #ddd;border-bottom: 1px solid #ddd;","总区服数"));
        context.append(th("border-left: 1px solid #ddd;border-bottom: 1px solid #ddd;","区服限制数"));
        return thead("font-size: 14px;",tr("",context.toString()));
    }


    /**
     * 数据每一行渲染
     * @param name
     * @param aleradyOpen
     * @param noOpen
     * @param aleradyAdd
     * @param limitNum
     * @param lineNum
     * @return
     */
    public static String drawTr(String name, int aleradyOpen, int noOpen, int aleradyAdd, int limitNum, int lineNum, boolean isLastTd, boolean isWarning){
        StringBuffer context = new StringBuffer();
        String firstLine = isLastTd ? "color: #2fa4e7;" : "border-bottom: 1px solid #ddd;color: #2fa4e7;";
        String otherLine = isLastTd ? "border-left: 1px solid #ddd;" : "border-left: 1px solid #ddd;border-bottom: 1px solid #ddd;";
        String otherWarnLine = isLastTd ? "border-left: 1px solid #ddd;color:#ff3300;font-weight: bold;" : "border-left: 1px solid #ddd;border-bottom: 1px solid #ddd;color:#ff3300;font-weight: bold;";
        context.append(td(firstLine,name));
        context.append(td(otherLine,Integer.toString(aleradyOpen)));

        // 此处如果自动开服的数量小于15，就
        context.append(td(isWarning ? otherWarnLine : otherLine,Integer.toString(noOpen)));
        context.append(td(otherLine,Integer.toString(aleradyAdd)));
        context.append(td(otherLine,Integer.toString(limitNum)));
        // 此处奇数行加深背景 偶数行淡化背景
        return tr(lineNum % 2 != 0 ? "background: #f5f5f5;" : "background: #fff;",context.toString());
    }

    /**
     * 渲染tbody
     * @param trList
     * @return
     */
    public static String drawTbody(List<String> trList){
        StringBuffer context = new StringBuffer();
        for(String tr : trList){
            context.append(tr);
        }
        return tbody("font-size: 14px;", context.toString());
    }

    /**
     * 渲染table
     * @param thead
     * @param tbody
     * @return
     */
    public static String drawTable(String thead, String tbody){
        return table("border: 1px solid #ddd;border-radius: 5px;", thead + tbody);
    }


    public static String draw(List<String> trList){
        return  drawTable(drawThead(),drawTbody(trList));
    }


}
