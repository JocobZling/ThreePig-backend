package cn.tp.repositories;

import cn.tp.entities.FaceClustering;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface FaceClusteringRepository extends JpaRepository<FaceClustering, Long> {

    FaceClustering findByPhotoId(Long photoId);

    @Query(value = "select *,max(position) from FaceClustering where clusteringId in(select id from Clustering where userId = ?1)group by clusteringId", nativeQuery = true)
    List<FaceClustering> findOneFaceClustering(Long userId);

    FaceClustering findTopByAirFaceId(String airFaceId);

}
