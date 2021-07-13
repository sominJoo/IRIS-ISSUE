package com.iris.irisissue.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

public class Issue {
    private String no;
    private String title;
    private String user;
    private List<String> label;
    private String state;
    private String created;
    private String cloased;
    private List<String> assign;

    public Issue() {
    }

    public Issue(String no, String title, String user, List<String> label, String state, String created, String cloased, List<String> assign) {
        this.no = no;
        this.title = title;
        this.user = user;
        this.label = label;
        this.state = state;
        this.created = created;
        this.cloased = cloased;
        this.assign = assign;
    }

    @Override
    public String toString() {
        return "Issue{" +
                "no='" + no + '\'' +
                ", title='" + title + '\'' +
                ", user='" + user + '\'' +
                ", label=" + label +
                ", state='" + state + '\'' +
                ", created='" + created + '\'' +
                ", cloased='" + cloased + '\'' +
                ", assign=" + assign +
                '}';
    }

    public List<String> getAssign() {
        return assign;
    }

    public void setAssign(List<String> assign) {
        this.assign = assign;
    }

    public String getNo() {
        return no;
    }

    public void setNo(String no) {
        this.no = no;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public List<String> getLabel() {
        return label;
    }

    public void setLabel(List<String> label) {
        this.label = label;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public String getCloased() {
        return cloased;
    }

    public void setCloased(String cloased) {
        this.cloased = cloased;
    }
}
