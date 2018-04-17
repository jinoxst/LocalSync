package com.local.sync.employees;

import java.util.ArrayList;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import com.local.sync.employees.util.DBUtil;
import com.local.sync.employees.util.Shop;
import com.local.sync.employees.util.CommonUtil;

public class Main {
    public static Logger logger = Logger.getLogger("LOG");
    private DBUtil db;
    public static ArrayList<Shop> shops = new ArrayList<Shop>();
    private int threadpoolcnt;
    public static String mysqlIp;
    public static int port;
    public static String database;
    public static String user;
    public static String password;
    public static int mssqlport;
    public static String mssqldatabase;
    public static String mssqluser;
    public static String mssqlpassword;
    public static String driverPoolName = "EmployeesPool";
    public static int reportId;

    public Main(){
        try{
            Properties props = new Properties();
            props.loadFromXML(Thread.currentThread().getContextClassLoader().getResourceAsStream("config.xml"));
            this.threadpoolcnt = Integer.parseInt(props.getProperty("threadpoolcnt"));
            mysqlIp = props.getProperty("ip");
            port = Integer.parseInt(props.getProperty("port"));
            database = props.getProperty("database");
            user = props.getProperty("user");
            password = props.getProperty("password");
            mssqlport = Integer.parseInt(props.getProperty("mssqlport"));
            mssqldatabase = props.getProperty("mssqldatabase");
            mssqluser = props.getProperty("mssqluser");
            mssqlpassword = props.getProperty("mssqlpassword");
            db = new DBUtil();
        }catch(Exception e){
            logger.error(e);
        }
    }

    private void exec(){
        logger.debug("theadpoolcnt:" + threadpoolcnt);
        ExecutorService executorService = Executors.newFixedThreadPool(threadpoolcnt);

        logger.debug("JOB START");
        shops = db.getShops();

        int shopTargetCnt = CommonUtil.getShopTargetCnt();
        while(shopTargetCnt > 0){
            Shop s = CommonUtil.getTargetShop();
            executorService.execute(new Worker(db, s));
            shopTargetCnt = CommonUtil.getShopTargetCnt();
        }
        executorService.shutdown();
        try {
            executorService.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        }catch(InterruptedException e){
            logger.error(e);
        }
        logger.debug("JOB END");
    }

    public static void main(String[] args) {
        if(args != null && args.length == 1 && args[0].trim().length() > 0){
            Main.reportId = Integer.parseInt(args[0].trim());
            logger.info("report id:" + Main.reportId);
            Main main = new Main();
            if(CommonUtil.isAlreadyRunning()){
                logger.error("I am aleady runned by other process. Do it later");
            }else{
                main.exec();
            }
        }else {
            System.out.println("Usage "+ Main.class.getName() +" id");
        }
    }
}
