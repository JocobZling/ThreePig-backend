package cn.tp.repositories;

import cn.tp.entities.Clustering;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ClusteringRepository extends JpaRepository<Clustering, Long> {

    @Query(value = "SELECT id FROM Clustering")
    List<Long> findAllIds();
}
