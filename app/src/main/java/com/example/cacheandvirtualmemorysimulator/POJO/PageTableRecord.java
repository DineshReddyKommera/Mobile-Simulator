package com.example.cacheandvirtualmemorysimulator.POJO;

public class PageTableRecord {

    String index;
    String validBit;
    String physicalPage;

    public PageTableRecord(String index, String validBit, String physicalPage) {
        this.index = index;
        this.validBit = validBit;
        this.physicalPage = physicalPage;
    }

    public String getIndex() {
        return index;
    }

    public void setIndex(String index) {
        this.index = index;
    }

    public String getValidBit() {
        return validBit;
    }

    public void setValidBit(String validBit) {
        this.validBit = validBit;
    }

    public String getPhysicalPage() {
        return physicalPage;
    }

    public void setPhysicalPage(String physicalPage) {
        this.physicalPage = physicalPage;
    }

}
