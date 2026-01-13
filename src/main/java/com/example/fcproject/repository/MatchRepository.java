package com.example.fcproject.repository;

import com.example.fcproject.model.FcUser;
import com.example.fcproject.model.Match;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 赛程数据访问接口，提供对Match实体的CRUD操作
 */
@Repository
public interface MatchRepository extends JpaRepository<Match, Integer> {

    /**
     * 根据比赛状态查询比赛列表
     * @param status 比赛状态：0-未开始，1-进行中，2-已结束
     * @return 符合条件的比赛列表
     */
    List<Match> findByStatus(Short status);

    /**
     * 根据比赛轮次查询比赛列表
     * @param round 比赛轮次
     * @return 符合条件的比赛列表
     */
    List<Match> findByRound(Integer round);

    /**
     * 查询指定时间范围内的比赛
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 符合条件的比赛列表
     */
    List<Match> findByMatchTimeBetween(LocalDateTime startTime, LocalDateTime endTime);

    /**
     * 根据比赛编号查询比赛
     * @param matchNo 比赛编号
     * @return 符合条件的比赛
     */
    Match findByMatchNo(String matchNo);

    /**
     * 查询未开始的比赛
     * @return 未开始的比赛列表
     */
    List<Match> findByStatusOrderByMatchTimeAsc(Short status);
    
    /**
     * 查询用户作为主队参加的比赛
     * @param homeTeam 用户对象
     * @return 用户作为主队的比赛列表
     */
    List<Match> findByHomeTeamAndStatus(FcUser homeTeam, Short status);
    
    /**
     * 查询用户作为客队参加的比赛
     * @param awayTeam 用户对象
     * @return 用户作为客队的比赛列表
     */
    List<Match> findByAwayTeamAndStatus(FcUser awayTeam, Short status);
    
    /**
     * 查询用户作为主队或客队参加的比赛
     * @param homeTeam 用户对象（作为主队）
     * @param awayTeam 用户对象（作为客队）
     * @return 用户参与的所有比赛列表
     */
    List<Match> findByHomeTeamOrAwayTeam(FcUser homeTeam, FcUser awayTeam);
}