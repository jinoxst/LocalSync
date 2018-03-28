package com.local.sync.employees;

import com.local.sync.employees.util.DBUtil;
import com.local.sync.employees.util.Shop;

public class Worker implements Runnable {
    private DBUtil db;
    private Shop shop;

    public Worker(DBUtil d, Shop s){
        db = d;
        shop = s;
    }

    @Override
    public void run(){
        db.syncLocalEmployees(shop);
    }
}
