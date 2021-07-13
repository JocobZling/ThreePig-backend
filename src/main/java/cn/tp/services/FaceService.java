package cn.tp.services;

import cn.tp.entities.FaceClustering;
import cn.tp.entities.Photo;
import cn.tp.entities.vo.FacePositionAndNameAndList;
import cn.tp.entities.vo.PhotoDisplayVo;
import cn.tp.repositories.ClusteringRepository;
import cn.tp.repositories.FaceClusteringRepository;
import cn.tp.repositories.PhotoRepository;
import cn.tp.utils.FaceUtil;
import cn.tp.utils.FileUtil;
import com.alibaba.fastjson.JSONObject;
import com.chinamobile.bcop.api.sdk.ai.core.model.CommonJsonObjectResponse;
import com.chinamobile.bcop.api.sdk.ai.facebody.AiFaceBody;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class FaceService {

    private final FaceClusteringRepository faceClusteringRepository;

    private final PhotoRepository photoRepository;

    private final ClusteringRepository clusteringRepository;

    @Value("${photoAddr}")
    private String photoAddr;


    public FaceService(FaceClusteringRepository faceClusteringRepository, PhotoRepository photoRepository,ClusteringRepository clusteringRepository) {
        this.faceClusteringRepository = faceClusteringRepository;
        this.photoRepository = photoRepository;
        this.clusteringRepository = clusteringRepository;
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
            if (results.size() <= 7)
                results.add(faceClustering);
        });
        return results;
    }

    public List<FacePositionAndNameAndList> findOneKlassAllPhotoByUserIdAndClusteringId(Long userId, Long clusteringId) {
        List<FacePositionAndNameAndList> result = new ArrayList<>();
        String clusteringName = clusteringRepository.findName(clusteringId,userId);
        List<String> facePos = faceClusteringRepository.findPositionWhereUserIdAndClusteringId(clusteringId,userId);
        String facePosition = facePos.get(0);
        List<Photo> photoList = photoRepository.findPhotoByClusteringIdAndUserId(clusteringId, userId);
        FacePositionAndNameAndList facePositionAndNameAndList = new FacePositionAndNameAndList();
        facePositionAndNameAndList.setFacePosition(facePosition);
        facePositionAndNameAndList.setClusteringName(clusteringName);
        facePositionAndNameAndList.setPhotoDisplayVoList(getPhotoDisplayList(photoList));

        result.add(facePositionAndNameAndList);
        return result;

    }
    public boolean updateClusteringName(Long userId,Long clusteringId,String name)
    {
        try {
            clusteringRepository.updateClusterName(clusteringId,userId,name);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return false;
        }

        return true;
    }


    public FaceClustering findFaceClusteringByFaceId(Integer airFaceId) {
        return faceClusteringRepository.findTopByAirFaceId(String.valueOf(airFaceId));
    }

    public List<PhotoDisplayVo> getPhotoDisplayList(List<Photo> photoList) {
        return getPhotoDisplayVos(photoList, photoAddr);
    }

    static List<PhotoDisplayVo> getPhotoDisplayVos(List<Photo> photoList, String photoAddr) {
        List<PhotoDisplayVo> photoDisplayList = new ArrayList<>();
        photoList.forEach(photo -> {
            String position = photo.getPosition();
            String path = position.split("/images/")[1];
            try {
                List<String> imageInfo = FileUtil.getPictureSize(photoAddr + path);
                PhotoDisplayVo photoDisplayVo = new PhotoDisplayVo();
                photoDisplayVo.setHeight(imageInfo.get(1));
                photoDisplayVo.setWidth(imageInfo.get(0));
                photoDisplayVo.setSrc(photo.getPosition());
                photoDisplayList.add(photoDisplayVo);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        return photoDisplayList;
    }

}


