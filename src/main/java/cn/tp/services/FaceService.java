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

import java.util.List;

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

    public List<FaceClustering> getAllClusteringOneFaceByUserId(Long userId) {
        return faceClusteringRepository.findOneFaceClustering(userId);
    }

    public List<Photo> findOneKlassAllPhotoByUserIdAndClusteringId(Long userId, Long clusteringId) {
        return photoRepository.findPhotoByClusteringIdAndUserId(clusteringId, userId);
    }
}


