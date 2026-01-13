package com.example.fcproject.component;

import com.example.fcproject.model.Permission;
import com.example.fcproject.repository.PermissionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * 权限初始化组件，用于在应用启动时初始化系统权限数据
 */
@Component
public class InitPermissions implements CommandLineRunner {

    @Autowired
    private PermissionRepository permissionRepository;

    @Override
    public void run(String... args) throws Exception {
        // 检查是否已存在权限数据
        long count = permissionRepository.count();
        if (count > 0) {
            System.out.println("权限数据已存在，跳过初始化");
            return;
        }

        System.out.println("开始初始化权限数据...");

        // 定义系统权限列表
        List<Permission> permissions = new ArrayList<>();

        // 顶级菜单 - 比赛管理 (ID: 1)
        permissions.add(createPermission("match_manage", "比赛管理", "/match-score", 0L, 1, true, "比赛管理相关功能"));
        permissions.add(createPermission("match_list", "比赛列表", "/match-score", 1L, 1, true, "查看比赛列表"));
        permissions.add(createPermission("match_edit", "编辑比赛", "/match-score", 1L, 2, true, "编辑比赛信息"));
        permissions.add(createPermission("match_score", "录入比分", "/match-score", 1L, 3, true, "录入比赛比分"));

        // 顶级菜单 - 淘汰赛管理 (ID: 5)
        permissions.add(createPermission("tournament_manage", "淘汰赛管理", "/tournament-score", 0L, 2, true, "淘汰赛管理相关功能"));
        permissions.add(createPermission("tournament_list", "淘汰赛列表", "/tournament-score", 5L, 1, true, "查看淘汰赛列表"));
        permissions.add(createPermission("tournament_score", "录入淘汰赛比分", "/tournament-score", 5L, 2, true, "录入淘汰赛比分"));

        // 顶级菜单 - 数据统计 (ID: 8)
        permissions.add(createPermission("statistics_manage", "数据统计", "/rank-table", 0L, 3, true, "数据统计相关功能"));
        permissions.add(createPermission("rank_table", "积分排名", "/rank-table", 8L, 1, true, "查看积分排名"));
        permissions.add(createPermission("striker_table", "射手榜", "/striker-table", 8L, 2, true, "查看射手榜"));

        // 顶级菜单 - 赛程管理 (ID: 11)
        permissions.add(createPermission("schedule_manage", "赛程管理", "/schedule-generator", 0L, 4, true, "赛程管理相关功能"));
        permissions.add(createPermission("schedule_generate", "生成赛程", "/schedule-generator", 11L, 1, true, "生成比赛赛程"));
        permissions.add(createPermission("schedule_view", "查看赛程", "/simple-schedule", 11L, 2, true, "查看比赛赛程"));

        // 顶级菜单 - 用户管理 (ID: 14)
        permissions.add(createPermission("user_manage", "用户管理", "/fc-user", 0L, 5, true, "用户管理相关功能"));
        permissions.add(createPermission("user_list", "用户列表", "/fc-user", 14L, 1, true, "查看用户列表"));
        permissions.add(createPermission("user_edit", "编辑用户", "/fc-user", 14L, 2, true, "编辑用户信息"));

        // 顶级菜单 - 审核管理 (ID: 17)
        permissions.add(createPermission("audit_manage", "审核管理", "/audit-page", 0L, 6, true, "审核管理相关功能"));
        permissions.add(createPermission("audit_list", "审核列表", "/audit-page", 17L, 1, true, "查看审核列表"));
        permissions.add(createPermission("audit_handle", "处理审核", "/audit-page", 17L, 2, true, "处理审核请求"));

        // 顶级菜单 - 系统管理 (ID: 20)
        permissions.add(createPermission("system_manage", "系统管理", "/", 0L, 7, true, "系统管理相关功能"));
        permissions.add(createPermission("permission_config", "权限配置", "/permission-config", 20L, 1, true, "配置用户权限"));

        // 保存权限数据
        permissionRepository.saveAll(permissions);

        System.out.println("权限数据初始化完成，共初始化 " + permissions.size() + " 个权限");
    }

    /**
     * 构造方法，用于创建权限对象
     * @param code 权限代码
     * @param name 权限名称
     * @param url 权限URL
     * @param parentId 父权限ID
     * @param order 排序
     * @param isEnabled 是否启用
     * @param description 描述
     */
    private Permission createPermission(String code, String name, String url, Long parentId, Integer order, Boolean isEnabled, String description) {
        Permission permission = new Permission();
        permission.setCode(code);
        permission.setName(name);
        permission.setUrl(url);
        permission.setParentId(parentId);
        permission.setOrder(order);
        permission.setIsEnabled(isEnabled);
        permission.setDescription(description);
        return permission;
    }
}