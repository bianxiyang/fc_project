package com.example.fcproject.model;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "fc_user")
public class FcUser {

    @Id
    @Column(name = "id")
    private Integer id;

    @Column(name = "name", length = 255)
    private String name;

    @Column(name = "username", length = 100, unique = true)
    private String username;

    @Column(name = "password", length = 255)
    private String password;

    @Column(name = "role", length = 50)
    private String role = "USER";

    @Column(name = "win")
    private Short win = 0;

    @Column(name = "tie")
    private Short tie = 0;

    @Column(name = "lose")
    private Short lose = 0;

    @Column(name = "powerful")
    private Integer powerful;

    @Column(name = "avatar", length = 255)
    private String avatar; // 头像URL

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserPermission> userPermissions;

    // Getters and Setters
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

    public Short getWin() {
        return win;
    }

    public void setWin(Short win) {
        this.win = win;
    }

    public Short getTie() {
        return tie;
    }

    public void setTie(Short tie) {
        this.tie = tie;
    }

    public Short getLose() {
        return lose;
    }

    public void setLose(Short lose) {
        this.lose = lose;
    }

    public Integer getPowerful() {
        return powerful;
    }

    public void setPowerful(Integer powerful) {
        this.powerful = powerful;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public List<UserPermission> getUserPermissions() {
        return userPermissions;
    }

    public void setUserPermissions(List<UserPermission> userPermissions) {
        this.userPermissions = userPermissions;
    }
    
    /**
     * 计算总场次（胜场+平场+负场）
     * @return 总场次
     */
    public int getTotalMatches() {
        return (win != null ? win : 0) + (tie != null ? tie : 0) + (lose != null ? lose : 0);
    }
}