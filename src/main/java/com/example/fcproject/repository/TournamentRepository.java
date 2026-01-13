package com.example.fcproject.repository;

import com.example.fcproject.model.Tournament;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * 锦标赛数据访问接口，提供对Tournament实体的CRUD操作
 */
@Repository
public interface TournamentRepository extends JpaRepository<Tournament, Integer> {
}
