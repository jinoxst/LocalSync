package com.local.sync.employees.util;

public class Shop {
    private int idx;
    private int accCd;
    private String ip;
    private int step;//0:job target, 1:job started, 2:job end

    public Shop(){
    }

    public Shop(int idx, int accCd, String ip){
        this.idx = idx;
        this.accCd = accCd;
        this.ip = ip;
    }

    public int getIdx() {
        return idx;
    }

    public void setIdx(int idx) {
        this.idx = idx;
    }

    public int getAccCd() {
        return accCd;
    }
    public void setAccCd(int accCd) {
        this.accCd = accCd;
    }
    public String getIp() {
        return ip;
    }
    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getStep() {
        return step;
    }

    public void setStep(int step) {
        this.step = step;
    }
}
