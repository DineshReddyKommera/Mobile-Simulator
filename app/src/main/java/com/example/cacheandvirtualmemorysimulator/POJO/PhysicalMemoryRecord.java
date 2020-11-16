package com.example.cacheandvirtualmemorysimulator.POJO;

public class PhysicalMemoryRecord {
    String physicalPage;
    String content;

    public PhysicalMemoryRecord(String physicalPage, String content) {
        this.physicalPage = physicalPage;
        this.content = content;
    }

    public String getPhysicalPage() {
        return physicalPage;
    }

    public void setPhysicalPage(String physicalPage) {
        this.physicalPage = physicalPage;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
