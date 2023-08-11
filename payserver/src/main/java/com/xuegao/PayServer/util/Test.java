package com.xuegao.PayServer.util;

import com.xuegao.PayServer.util.huaweipay.RSA;

import java.util.concurrent.locks.LockSupport;

public class Test {
    public static void main(String[] args) {
        String sourceStr="GST1909120000226|729272C1EB489D917F52CFAFCF84B27649CF2A26A43FF6BDE4D05E338FA88BA2A533D3EDEEA74A63D14549F154416A49AD234E246B5B962B000828CE55BB53F5731D016FB2A493553D1D70976CBD71EC|";
        String[] source = sourceStr.split("\\|");

        String fs=null;
        if(source.length==2){ fs="null";}
        String  trade = new StringBuffer().append(fs).append(",")
                    .append(source[0]).append(",")
                    .toString();

        System.out.print(trade);


    }
}
//OvuanwCV2UgQoY+uBdHBBEMNCUWIqnscbJoDgQ8bfzAP7Ek9zQwm2x2qS2j/gGSr1a0sbig4ABX/Vru2QpdCSuqzcbE7xNTOtbK1Hob3Nz4wz2Kjm5bC1in7PVcv70Y5THxYhycsTULG5NV97HFB3L8PGBJ5Kal/QpaMsnsvnKnrqYAHqnfjG6C7xPMNQ5MwSSOBJ/Wi2a7vw1CanPR8+D4QsPVaNXdL0VfQ6MB3H9j5empj4+RT2EKpYzY0FHJhQ5IkycenMVuceSP++C3GLX9I0OVgJ2/6a/U9vGOS24eskOE+Tq9aksWQJ3/SV0GhBIYhVeutpWua8M1USMhQ3A==
//OvuanwCV2UgQoY+uBdHBBEMNCUWIqnscbJoDgQ8bfzAP7Ek9zQwm2x2qS2j/gGSr1a0sbig4ABX/Vru2QpdCSuqzcbE7xNTOtbK1Hob3Nz4wz2Kjm5bC1in7PVcv70Y5THxYhycsTULG5NV97HFB3L8PGBJ5Kal/QpaMsnsvnKnrqYAHqnfjG6C7xPMNQ5MwSSOBJ/Wi2a7vw1CanPR8+D4QsPVaNXdL0VfQ6MB3H9j5empj4+RT2EKpYzY0FHJhQ5IkycenMVuceSP++C3GLX9I0OVgJ2/6a/U9vGOS24eskOE+Tq9aksWQJ3/SV0GhBIYhVeutpWua8M1USMhQ3A==