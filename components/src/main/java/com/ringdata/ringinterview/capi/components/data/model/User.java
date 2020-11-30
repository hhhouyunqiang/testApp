package com.ringdata.ringinterview.capi.components.data.model;

/**
 * Created by admin on 17/12/9.
 */

public class User {
    private Integer id;
    private String name;
    private Integer role;
    private String password;
    private String avatarPath;

    private String orgCode;
    private String orgHost;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getRole() {
        return role;
    }

    public void setRole(Integer role) {
        this.role = role;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getAvatarPath() {
        return avatarPath;
    }

    public void setAvatarPath(String avatarPath) {
        this.avatarPath = avatarPath;
    }

    public String getOrgCode() {
        return orgCode;
    }

    public void setOrgCode(String orgCode) {
        this.orgCode = orgCode;
    }

    public String getOrgHost() {
        return orgHost;
    }

    public void setOrgHost(String orgHost) {
        this.orgHost = orgHost;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", role='" + role + '\'' +
                ", password='" + password + '\'' +
                ", avatarPath='" + avatarPath + '\'' +
                ", orgCode='" + orgCode + '\'' +
                ", orgHost='" + orgHost + '\'' +
                '}';
    }
}
