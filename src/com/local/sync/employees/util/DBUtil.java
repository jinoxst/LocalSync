package com.local.sync.employees.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import org.apache.commons.dbcp.ConnectionFactory;
import org.apache.commons.dbcp.DriverManagerConnectionFactory;
import org.apache.commons.dbcp.PoolableConnectionFactory;
import org.apache.commons.dbcp.PoolingDriver;
import org.apache.commons.pool.ObjectPool;
import org.apache.commons.pool.impl.GenericObjectPool;
import org.apache.commons.pool.impl.GenericObjectPool.Config;
import org.apache.log4j.Logger;

import com.local.sync.employees.Main;

public class DBUtil {
    Logger logger = Logger.getLogger("LOG");

    @SuppressWarnings("unused")
    public DBUtil(){
        try {
          //DBCP-START
            Class.forName("com.mysql.jdbc.Driver");
            Config poolsConfig = new Config();
            poolsConfig.maxActive = 10;//アクティブなコネクションの最大数
            poolsConfig.whenExhaustedAction = GenericObjectPool.WHEN_EXHAUSTED_BLOCK;//コネクションが使い尽くされている場合の処理, 0 = 失敗, 1 = 待機, 2 = 新たに生成
            poolsConfig.maxWait = 2000;//最長待機時間
            poolsConfig.maxIdle = 10;//アイドル状態のコネクションの最大数
            poolsConfig.testOnBorrow = true;//コネクションを取得するときにテストするか
            poolsConfig.testOnReturn = true;//コネクションを返すときにテストするか
            poolsConfig.timeBetweenEvictionRunsMillis = 10000L;//プール内オブジェクト排除スレッドの実行間隔
            poolsConfig.numTestsPerEvictionRun = 5;//排除スレッドが一度のチェックで対象とするコネクションの数
            poolsConfig.minEvictableIdleTimeMillis = 2000L;//排除対象となるまでの最短アイドル時間
            poolsConfig.testWhileIdle = true;//アイドル状態のコネクションのテストを行うか
            ObjectPool connectionPool = new GenericObjectPool(null, poolsConfig);
            String connectURI = "jdbc:mysql://"+Main.mysqlIp+":"+Main.port+"/"+Main.database+"?user="+Main.user+"&password="+Main.password+"&characterEncoding=sjis&jdbcCompliantTruncation=false";
            ConnectionFactory connectionFactory = new DriverManagerConnectionFactory(connectURI,null);
            String validationQuery = "select count(*) from dual";
            PoolableConnectionFactory poolableConnectionFactory = new PoolableConnectionFactory(connectionFactory,connectionPool,null,validationQuery,false,true);
            Class.forName("org.apache.commons.dbcp.PoolingDriver");
            PoolingDriver driver = (PoolingDriver) DriverManager.getDriver("jdbc:apache:commons:dbcp:");
            driver.registerPool(Main.driverPoolName,connectionPool);
            //DBCP-END
        }catch(Exception e){
            logger.error(e.getMessage(),e);
        }
    }

    public ArrayList<Shop> getShops() {
        Connection conn = null;
        ResultSet rs = null;
        PreparedStatement pstmt = null;
        ArrayList<Shop> ips = new ArrayList<Shop>();
        try {
            conn = getConnection();
            StringBuffer buffer = new StringBuffer();
            buffer.append(" select @N := @N + 1 as idx, a.acc_cd, a.shop_ip ");
            buffer.append("   from TBL_SHOP a, (SELECT @N := 0) AS DUMMY_N ");
            buffer.append("  where a.sale_st_date <= current_date ");
            buffer.append("    and a.acc_cd not in (select b.acc_cd from TBL_SHOP_RENEWAL b where a.acc_cd=b.acc_cd and b.rdate=date_format(current_date, '%Y%m%d')) ");
            buffer.append("  order by a.acc_cd ");
            String query = buffer.toString();
            pstmt = conn.prepareStatement(query);
            rs = pstmt.executeQuery();
            while(rs.next()){
                Shop shop = new Shop();
                shop.setIdx(rs.getInt("idx"));
                shop.setAccCd(rs.getInt("acc_cd"));
                shop.setIp(rs.getString("shop_ip"));
                ips.add(shop);
            }
        }catch(Exception e){
            logger.error(e.getMessage(),e);
        }finally{
            if(rs != null){
                try{rs.close();}catch(Exception e){logger.error(e.getMessage(),e);}
            }
            if(pstmt != null){
                try{pstmt.close();}catch(Exception e){logger.error(e.getMessage(),e);}
            }
            if(conn != null){
                try{conn.close();}catch(Exception e){logger.error(e.getMessage(),e);}
            }
        }

        return ips;
    }

    public Connection getConnection() {
        Connection conn = null;
        try {
            conn = (Connection) DriverManager.getConnection("jdbc:apache:commons:dbcp:"+Main.driverPoolName);
        }catch(Exception e){
            logger.error(e.getMessage(),e);
        }
        return conn;
    }

    public Connection getMssqlConnection(String ip) throws SQLException{
        Connection conn = null;
        try {
            conn = (Connection) DriverManager.getConnection("jdbc:sqlserver://"+ip+":"+Main.mssqlport+";DatabaseName="+Main.mssqldatabase,Main.mssqluser,Main.mssqlpassword);
        }catch(SQLException s){
            throw s;
        }catch(Exception e){
            logger.error(e.getMessage(),e);
        }
        return conn;
    }

    private void report(Shop shop, int code, String msg) {
        Connection conn = null;
        ResultSet rs = null;
        PreparedStatement pstmt = null;
        try {
            conn = getConnection();
            StringBuffer buffer = new StringBuffer();
            buffer.append(" update EmployeesSyncReport set code=?, msg=? where id=? and acc_cd=? ");
            String query = buffer.toString();
            pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, code);
            pstmt.setString(2, msg);
            pstmt.setInt(3, Main.reportId);
            pstmt.setInt(4, shop.getAccCd());
            pstmt.executeUpdate();
        }catch(Exception e){
            logger.error(e.getMessage(),e);
        }finally{
            if(rs != null){
                try{rs.close();}catch(Exception e){logger.error(e.getMessage(),e);}
            }
            if(pstmt != null){
                try{pstmt.close();}catch(Exception e){logger.error(e.getMessage(),e);}
            }
            if(conn != null){
                try{conn.close();}catch(Exception e){logger.error(e.getMessage(),e);}
            }
        }
    }

    public void syncLocalEmployees(Shop shop) {
        Connection mssqlConn = null;
        PreparedStatement pstmt = null;
        try {
            mssqlConn = getMssqlConnection(shop.getIp());
            String query = "exec usp_EmployeesUpdate";
            pstmt = mssqlConn.prepareStatement(query);
            boolean result = pstmt.execute();
            if(!result) {
                logger.debug(shop.getAccCd()+"["+shop.getIdx()+"]["+shop.getIp()+"] updateCnt:"+pstmt.getUpdateCount());
            }
            report(shop, 1, "OK");
        }catch(SQLException s){
            report(shop, -1, s.getMessage());
            logger.error("["+shop.getAccCd()+"]["+shop.getIp()+"]-"+s.getMessage());
        }catch(Exception e){
            report(shop, -1, e.getMessage());
            logger.error(e.getMessage(),e);
        }finally{
            if(pstmt != null){
                try{pstmt.close();}catch(Exception e){logger.error(e.getMessage(),e);}
            }
            if(mssqlConn != null){
                try{mssqlConn.close();}catch(Exception e){logger.error(e.getMessage(),e);}
            }

            shop.setStep(2);
        }
    }
}
