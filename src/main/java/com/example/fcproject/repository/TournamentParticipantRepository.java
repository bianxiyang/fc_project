package com.example.fcproject.repository;

import com.example.fcproject.model.TournamentParticipant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 锦标赛参与者数据访问接口，提供对TournamentParticipant实体的CRUD操作
 */
@Repository
public interface TournamentParticipantRepository extends JpaRepository<TournamentParticipant, Integer> {
    
    /**
     * 根据锦标赛ID查询参与者列表
     */
    List<TournamentParticipant> findByTournamentId(Integer tournamentId);
    
    /**
     * 根据锦标赛ID和用户ID查询参与者
     */
    TournamentParticipant findByTournamentIdAndUserId(Integer tournamentId, Integer userId);
}
