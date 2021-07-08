package cn.tp.services;

import cn.tp.entities.FaceClustering;
import cn.tp.entities.Photo;
import cn.tp.exceptions.BusinessException;
import cn.tp.repositories.ClusteringRepository;
import cn.tp.repositories.FaceClusteringRepository;
import cn.tp.repositories.PhotoRepository;
import cn.tp.utils.FaceUtil;
import com.alibaba.fastjson.JSONObject;
import com.chinamobile.bcop.api.sdk.ai.core.constant.Region;
import com.chinamobile.bcop.api.sdk.ai.core.model.CommonJsonObjectResponse;
import com.chinamobile.bcop.api.sdk.ai.facebody.AiFaceBody;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class FaceService {

    private final FaceClusteringRepository faceClusteringRepository;

    private final ClusteringRepository clusteringRepository;

    private final PhotoRepository photoRepository;


    public FaceService(FaceClusteringRepository faceClusteringRepository, ClusteringRepository clusteringRepository, PhotoRepository photoRepository) {
        this.faceClusteringRepository = faceClusteringRepository;
        this.clusteringRepository = clusteringRepository;
        this.photoRepository = photoRepository;
    }

    public JSONObject getFaceDetection(String face) {
        //人脸识别与人体识别
        AiFaceBody aiFaceBody = FaceUtil.getInstance();
        try {
            CommonJsonObjectResponse response = aiFaceBody.faceDetect(face, null);
            return response.getCommonResult();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public double compareFace(String face1, String face2) {
        AiFaceBody aiFaceBody = FaceUtil.getInstance();
        try {
            CommonJsonObjectResponse response = aiFaceBody.faceMatch(face1, face2, null);
            return Double.parseDouble(String.valueOf(response.getCommonResult().get("confidence")));
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    public String createFaceSet(String name) {
        AiFaceBody aiFaceBody = FaceUtil.getInstance();
        try {
            CommonJsonObjectResponse response = aiFaceBody.createFaceSet(name, "", String.valueOf(aiFaceBody.getAccessToken()), null);
            return String.valueOf(response.getCommonResult().get("faceStoreId"));
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public Map<?, ?> searchFace(String position, Integer storeId) {
        AiFaceBody aiFaceBody = FaceUtil.getInstance();
        try {
            CommonJsonObjectResponse response = aiFaceBody.faceSearch(position, String.valueOf(storeId), 1, String.valueOf(aiFaceBody.getAccessToken()), null);
            ArrayList<?> results = (ArrayList<?>) response.getCommonResult().get("results");
            return (Map<?, ?>) results.get(0);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public String addFaceToAirSet(Integer airSetId, String position, String name) {
        AiFaceBody aiFaceBody = FaceUtil.getInstance();
        try {
            CommonJsonObjectResponse response = aiFaceBody.createFaceToFile(airSetId, position, name, "", String.valueOf(aiFaceBody.getAccessToken()), null);
            return String.valueOf(response.getCommonResult().get("faceId"));
        } catch (Exception e) {
            e.printStackTrace();
            return "error";
        }
    }

    public List<FaceClustering> getAllClusteringOneFaceByUserId(Long userId) {
        return faceClusteringRepository.findOneFaceClustering(userId);
    }

    public List<FaceClustering> getEightClusteringOneFaceByUserId(Long userId) {
        List<FaceClustering> results = new ArrayList<>();
        getAllClusteringOneFaceByUserId(userId).forEach(faceClustering -> {
            if (results.size() <= 8)
                results.add(faceClustering);
        });
        return results;
    }

    public List<Photo> findOneKlassAllPhotoByUserIdAndClusteringId(Long userId, Long clusteringId) {
        return photoRepository.findPhotoByClusteringIdAndUserId(clusteringId, userId);
    }

    public FaceClustering findFaceClusteringByFaceId(Integer airFaceId) {
        return faceClusteringRepository.findTopByAirFaceId(String.valueOf(airFaceId));
    }
}


