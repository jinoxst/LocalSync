package com.local.sync.employees.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.log4j.Logger;

import com.local.sync.employees.Main;

public class CommonUtil {
    public static Logger logger = Logger.getLogger("LOG");

    public static int getShopTargetCnt(){
        int cnt = 0;
        for(Shop shop : Main.shops){
            if(shop.getStep() == 0){
                cnt++;
            }

            if(cnt > 0){
                break;
            }
        }

        return cnt;
    }

    public static Shop getTargetShop(){
        Shop s = null;
        for(Shop shop : Main.shops){
            if(shop.getStep() == 0){
                s = shop;
                break;
            }
        }
        s.setStep(1);
        return s;
    }

    public static boolean isAlreadyRunning(){
        boolean running = false;
        int cnt = 0;
        try{
            String[] Command = {
                "sh",
                "-c",
                "ps ww -f -C java | grep " + Main.class.getName()
            };
            Process p = Runtime.getRuntime().exec(Command);
            InputStream is = p.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            String line = null;
            while((line = br.readLine()) != null){
                line = line.trim().replaceAll("\\s{2,}", " ");
                String arr[] = line.split(" ");
                String compack = line.substring(line.indexOf(Main.class.getName()));
                if(arr != null && arr.length > 2){
                    logger.debug("uid:[" + arr[0] + "] pid:[" + arr[1] + "] " + compack);
                }else{
                    logger.debug(compack);
                }
                String compackArr[] = compack.split(" ");
                if(compackArr[0].equals(Main.class.getName())){
                    cnt++;
                }
            }

            logger.debug("cnt:" + cnt);
            if(cnt > 1){ // cnt=1 means myself only. cnt>1 means I am already running
                running = true;
            }
        }catch(IOException e){
            logger.error(e.getMessage(), e);
        }
        return running;
    }
}
