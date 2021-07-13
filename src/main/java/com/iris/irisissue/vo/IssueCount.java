package com.iris.irisissue.vo;

public class IssueCount {
    private String name;
    private int total;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }



    public IssueCount() {

    }
    public IssueCount(String name, int total) {
        this.name = name;
        this.total = total;
    }

    @Override
    public String toString() {
        return "IssueCount{" +
                "name='" + name + '\'' +
                ", total=" + total +
                '}';
    }
}
