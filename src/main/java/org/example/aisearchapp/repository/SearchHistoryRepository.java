package org.example.aisearchapp.repository;

import org.example.aisearchapp.model.SearchHistory;
import org.example.aisearchapp.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface SearchHistoryRepository extends JpaRepository<SearchHistory, Long> {

    List<SearchHistory> findByUserOrderBySearchTimestampDesc(User user);


    @Transactional
    void deleteByUser(User user);

}
