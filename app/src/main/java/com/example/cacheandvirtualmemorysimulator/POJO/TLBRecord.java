package com.example.cacheandvirtualmemorysimulator.POJO;

public class TLBRecord {

    int index;
    String virtualPage;
    String physicalPage;

    public TLBRecord(int index, String virtualPage, String physicalPage) {
        this.index = index;
        this.virtualPage = virtualPage;
        this.physicalPage = physicalPage;
    }
    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getVirtualPage() {
        return virtualPage;
    }

    public void setVirtualPage(String virtualPage) {
        this.virtualPage = virtualPage;
    }

    public String getPhysicalPage() {
        return physicalPage;
    }

    public void setPhysicalPage(String physicalPage) {
        this.physicalPage = physicalPage;
    }

}
