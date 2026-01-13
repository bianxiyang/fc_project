package com.example.fcproject.repository;

import com.example.fcproject.model.TournamentMatchScoreAudit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TournamentMatchScoreAuditRepository extends JpaRepository<TournamentMatchScoreAudit, Integer> {
    
    // 根据审核状态查询审核记录
    List<TournamentMatchScoreAudit> findByAuditStatus(String auditStatus);
    
    // 根据比赛ID查询审核记录
    List<TournamentMatchScoreAudit> findByMatchId(Integer matchId);
    
    // 根据比赛ID和审核状态查询审核记录
    List<TournamentMatchScoreAudit> findByMatchIdAndAuditStatus(Integer matchId, String auditStatus);
}