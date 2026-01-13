package com.example.fcproject.repository;

import com.example.fcproject.model.TournamentMatch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 锦标赛比赛数据访问接口，提供对TournamentMatch实体的CRUD操作
 */
@Repository
public interface TournamentMatchRepository extends JpaRepository<TournamentMatch, Integer> {
    
    /**
     * 根据锦标赛ID查询所有比赛
     */
    List<TournamentMatch> findByTournamentId(Integer tournamentId);
    
    /**
     * 根据锦标赛ID和轮次查询比赛
     */
    List<TournamentMatch> findByTournamentIdAndRound(Integer tournamentId, Integer round);
    
    /**
     * 根据锦标赛ID、轮次和比赛编号查询比赛
     */
    List<TournamentMatch> findByTournamentIdAndRoundAndMatchNumber(Integer tournamentId, Integer round, Integer matchNumber);
    
    /**
     * 根据锦标赛ID和状态查询比赛
     */
    List<TournamentMatch> findByTournamentIdAndStatus(Integer tournamentId, Short status);
}
