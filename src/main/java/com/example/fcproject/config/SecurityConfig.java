package com.example.fcproject.config;

import com.example.fcproject.model.FcUser;
import com.example.fcproject.service.FcUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.http.HttpMethod;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Spring Security配置类，用于设置认证和授权规则
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private FcUserService fcUserService;

    /**
     * 配置密码编码器
     * 注意：NoOpPasswordEncoder仅用于开发测试，生产环境应使用BCryptPasswordEncoder
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return NoOpPasswordEncoder.getInstance();
    }

    /**
     * 配置用户详情服务
     */
    @Bean
    public UserDetailsService userDetailsService() {
        return username -> {
            FcUser user = fcUserService.getUserByUsername(username);
            if (user == null) {
                throw new UsernameNotFoundException("用户不存在");
            }
            
            // 加载用户的权限
            List<String> authorities = new ArrayList<>();
            
            // 添加角色作为权限
            authorities.add("ROLE_" + user.getRole());
            
            // 添加用户的具体权限
            try {
                List<com.example.fcproject.model.Permission> userPermissions = fcUserService.getPermissionsByUserId(user.getId());
                for (com.example.fcproject.model.Permission permission : userPermissions) {
                    authorities.add(permission.getCode());
                }
            } catch (Exception e) {
                // 权限加载失败时不影响用户登录
                e.printStackTrace();
            }
            
            // 将FcUser转换为Spring Security的UserDetails
            return org.springframework.security.core.userdetails.User.builder()
                    .username(user.getUsername())
                    .password(user.getPassword())
                    .authorities(authorities.toArray(new String[0]))
                    .build();
        };
    }

    /**
     * 配置认证管理器
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    /**
     * 配置认证成功处理器，根据用户角色跳转到不同页面
     */
    @Bean
    public AuthenticationSuccessHandler authenticationSuccessHandler() {
        return (request, response, authentication) -> {
            // 获取用户角色
            Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
            for (GrantedAuthority authority : authorities) {
                if (authority.getAuthority().equals("ROLE_USER")) {
                    // 普通用户跳转到积分榜
                    response.sendRedirect("/rank-table");
                    return;
                } else if (authority.getAuthority().equals("ROLE_ADMIN")) {
                    // 管理员跳转到比赛比分
                    response.sendRedirect("/match-score");
                    return;
                } else if (authority.getAuthority().equals("ROLE_ROOT")) {
                    // 超级管理员跳转到审核页面
                    response.sendRedirect("/audit-page");
                    return;
                }
            }
            // 默认跳转
            response.sendRedirect("/");
        };
    }

    /**
     * 配置授权规则
     */
    @Bean
    public org.springframework.security.web.SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // 允许所有用户访问登录页面和静态资源
            .authorizeHttpRequests()
                // 允许所有用户访问的资源
                .antMatchers("/", "/login", "/css/**", "/js/**", "/tournament-tree").permitAll()
                // 淘汰赛树状图相关API对所有人开放
                .antMatchers("/api/tournaments", "/api/tournaments/**/bracket").permitAll()
                // API端点：比分保存相关操作，USER角色可以修改自己参与的比赛，ADMIN/ROOT可以修改所有比赛
                .antMatchers(HttpMethod.PUT, "/api/matches/**/score").hasAnyRole("USER", "ADMIN", "ROOT")
                // 创建比赛和生成赛程：管理员和超级管理员可以访问
                .antMatchers(HttpMethod.POST, "/api/matches/**").hasAnyRole("ADMIN", "ROOT")
                // 审核相关API：只有超级管理员可以访问
                .antMatchers("/api/matches/audit/**").hasRole("ROOT")
                // 淘汰赛审核相关API：只有超级管理员可以访问
                .antMatchers("/api/tournaments/audit/**").hasRole("ROOT")
                // 其他锦标赛相关API：已认证用户可以访问
                .antMatchers("/api/tournaments/**").hasAnyRole("USER", "ADMIN", "ROOT")
                // 其他API端点：已认证用户可以访问
                .antMatchers("/api/**").hasAnyRole("USER", "ADMIN", "ROOT")
                // 普通用户可以访问的页面
                .antMatchers("/rank-table", "/striker-table", "/ability-calculator", "/match-score", "/tournament-score").hasAnyRole("USER", "ADMIN", "ROOT")
                // 管理员可以访问的页面
                .antMatchers("/schedule-generator", "/simple-schedule", "/fc-user").hasAnyRole("ADMIN", "ROOT")
                // 超级管理员可以访问的页面
                .antMatchers("/audit-page", "/tournament-management", "/permission-config").hasRole("ROOT")
                // 所有请求都需要认证
                .anyRequest().authenticated()
                .and()
            // 配置登录表单
            .formLogin()
                .loginPage("/login")
                .loginProcessingUrl("/login")
                .successHandler(authenticationSuccessHandler())
                .failureUrl("/login?error=true")
                .permitAll()
                .and()
            // 配置登出
            .logout()
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login")
                .permitAll()
                .and()
            // 关闭CSRF保护（仅用于开发测试）
            .csrf().disable();
        return http.build();
    }
}