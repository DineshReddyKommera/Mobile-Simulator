package com.example.cacheandvirtualmemorysimulator.POJO;

public class CacheRecord {
    private int index;
    private boolean isValid;
    private String tag;
    private String data;
    private boolean dirtyBit;

    public CacheRecord(int index, boolean isValid, String tag, String data, boolean dirtyBit) {
        this.index = index;
        this.isValid = isValid;
        this.tag = tag;
        this.data = data;
        this.dirtyBit = dirtyBit;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public boolean isValid() {
        return isValid;
    }

    public void setValid(boolean valid) {
        isValid = valid;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public boolean isDirtyBit() {
        return dirtyBit;
    }

    public void setDirtyBit(boolean dirtyBit) {
        this.dirtyBit = dirtyBit;
    }
}
