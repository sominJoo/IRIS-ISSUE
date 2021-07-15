package com.iris.irisissue.vo;

import java.util.List;

public class IssuesExt extends Issue{
    private int perPage;
    private int currentPage;

    public int getPerPage() {
        return perPage;
    }

    public void setPerPage(int perPage) {
        this.perPage = perPage;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    @Override
    public String toString() {
        return "Issue{" +
                "no='" + getNo() + '\'' +
                ", title='" + getTitle() + '\'' +
                ", user='" + getUser() + '\'' +
                ", label=" + getLabel() +
                ", state='" + getState() + '\'' +
                ", created='" + getCreated() + '\'' +
                ", cloased='" + getCloased() + '\'' +
                ", assign=" + getAssign() +
                ", perPage=" + perPage +
                ", currentPage=" + currentPage +
                '}';
    }

    public IssuesExt() {
        super();
    }

    public IssuesExt(String no, String title, String user, List<String> label, String state, String created, String cloased, List<String> assign) {
        super(no, title, user, label, state, created, cloased, assign);
    }

    @Override
    public List<String> getAssign() {
        return super.getAssign();
    }

    @Override
    public void setAssign(List<String> assign) {
        super.setAssign(assign);
    }

    @Override
    public String getNo() {
        return super.getNo();
    }

    @Override
    public void setNo(String no) {
        super.setNo(no);
    }

    @Override
    public String getTitle() {
        return super.getTitle();
    }

    @Override
    public void setTitle(String title) {
        super.setTitle(title);
    }

    @Override
    public String getUser() {
        return super.getUser();
    }

    @Override
    public void setUser(String user) {
        super.setUser(user);
    }

    @Override
    public List<String> getLabel() {
        return super.getLabel();
    }

    @Override
    public void setLabel(List<String> label) {
        super.setLabel(label);
    }

    @Override
    public String getState() {
        return super.getState();
    }

    @Override
    public void setState(String state) {
        super.setState(state);
    }

    @Override
    public String getCreated() {
        return super.getCreated();
    }

    @Override
    public void setCreated(String created) {
        super.setCreated(created);
    }

    @Override
    public String getCloased() {
        return super.getCloased();
    }

    @Override
    public void setCloased(String cloased) {
        super.setCloased(cloased);
    }
}
