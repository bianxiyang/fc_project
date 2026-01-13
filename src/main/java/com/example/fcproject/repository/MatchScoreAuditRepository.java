package com.example.fcproject.repository;

import com.example.fcproject.model.MatchScoreAudit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MatchScoreAuditRepository extends JpaRepository<MatchScoreAudit, Integer> {
    
    // 根据审核状态查询审核记录
    List<MatchScoreAudit> findByAuditStatus(String auditStatus);
    
    // 根据比赛ID查询审核记录
    List<MatchScoreAudit> findByMatchId(Integer matchId);
    
    // 根据比赛ID和审核状态查询审核记录
    List<MatchScoreAudit> findByMatchIdAndAuditStatus(Integer matchId, String auditStatus);
}
