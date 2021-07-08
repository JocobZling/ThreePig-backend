package cn.tp.services;

import cn.tp.entities.Clustering;
import cn.tp.entities.FaceClustering;
import cn.tp.entities.Photo;
import cn.tp.entities.bo.FaceCompareConfidenceAndClusterIdBo;
import cn.tp.entities.bo.UserUploadPhotoBo;
import cn.tp.entities.vo.PhotoDateAndSorce;
import cn.tp.entities.vo.PhotoDisplayVo;
import cn.tp.repositories.ClusteringRepository;
import cn.tp.repositories.FaceClusteringRepository;
import cn.tp.repositories.PhotoRepository;
import cn.tp.repositories.PhotoTypeRepository;
import cn.tp.utils.FileUtil;
import cn.tp.utils.OpenCVUtil;
import com.alibaba.fastjson.JSONObject;
import com.chinamobile.cmss.sdk.ECloudDefaultClient;
import com.chinamobile.cmss.sdk.ECloudServerException;
import com.chinamobile.cmss.sdk.IECloudClient;
import com.chinamobile.cmss.sdk.http.constant.Region;
import com.chinamobile.cmss.sdk.http.signature.Credential;
import com.chinamobile.cmss.sdk.request.EngineImageClassifyDetectPostRequest;
import com.chinamobile.cmss.sdk.response.EngineImageClassifyDetectResponse;
import com.chinamobile.cmss.sdk.response.bean.EngineClassify;
import com.chinamobile.cmss.sdk.util.JacksonUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotNull;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class PhotoService {

    @Value("${ak}")
    private String ak;

    @Value("${sk}")
    private String sk;

    @Value("${photoAddr}")
    private String photoAddr;

    @Value("${faceAddr}")
    private String faceAddr;

    @Value("${imageUrl}")
    private String imageUrl;

    private final PhotoRepository photoRepository;

    private final FaceClusteringRepository faceClusteringRepository;

    private final ClusteringRepository clusteringRepository;

    private final PhotoTypeRepository photoTypeRepository;

    private final FaceService faceService;

    private final UserCenterService userCenterService;


    public PhotoService(PhotoRepository photoRepository, FaceService faceService, FaceClusteringRepository faceClusteringRepository, ClusteringRepository clusteringRepository, PhotoTypeRepository photoTypeRepository, UserCenterService userCenterService) {
        this.faceClusteringRepository = faceClusteringRepository;
        this.photoRepository = photoRepository;
        this.faceService = faceService;
        this.clusteringRepository = clusteringRepository;
        this.photoTypeRepository = photoTypeRepository;
        this.userCenterService = userCenterService;
    }

    //如果这张图的人脸个数大于10 不进行聚类 -> 归为群像
    // 如果 faceClustering 表为空 直接添加 创建 不调用 search
    // 切割人脸 -> 调用人脸search -> 返回匹配值低于70 or null -> 归为新类 -> 保存到人脸库里 -> 保存到本地
    // 返回值高于70 -> 获取faceId -> 去表里查到clusterId ->保存
    //如果faceClustering表不为空，调用faceClustering中的所有脸进行1v1比较，得出最大的分数就将该小脸归为该类，并记录该clustering
    //若最大分数小于多少？就将其定义一个新的clustering
    //保存到faceClustering表中
    //与所有该用户上传的小脸进行比对，比对分值高为？以上的分为一个类，若无则创建一个新的类
    public void savePhotoToFile(MultipartFile[] files, Long userId) throws Exception {
        String airSetId = userCenterService.findAirSetIdByUserId(userId);
        assert files != null || files.length >= 1;
        for (MultipartFile file : files) {
            String photoSavePosition = imageUrl + Objects.requireNonNull(FileUtil.saveFile(file, photoAddr)).get(0);
            String photoAddrPath = Objects.requireNonNull(FileUtil.saveFile(file, photoAddr)).get(1);
            Photo photo = new Photo();
            String colorScore = OpenCVUtil.calculateColorScore(photoAddrPath);
            String image = FileUtil.encryptToBase64(photoAddrPath);
            JSONObject response = faceService.getFaceDetection(photoAddrPath);
            double faceNum;
            if (response == null)
                faceNum = 0;
            else
                faceNum = Double.parseDouble(String.valueOf(response.get("faceNum")));
            if (faceNum > 0 && faceNum <= 10) {
                photo.setType("人物");
                photo.setFaceScore(String.valueOf(faceNum * 10));
                photo.setPosition(photoSavePosition);
                photo.setUserId(userId);
                photo.setColorScore(colorScore);
                photoRepository.save(photo);
                //获取一张图中所有的人脸faceDetectRectangleArea
                ArrayList<?> faceDetectDetailList = (ArrayList<?>) response.get("faceDetectDetailList");
                // ArrayList<String> positions = new ArrayList<>();
                List<FaceClustering> faceClusteringList = faceService.getAllClusteringOneFaceByUserId(userId);
                faceDetectDetailList.forEach(faceDetect -> {
                    Map<?, ?> detect = (Map<?, ?>) faceDetect;
                    Map<?, ?> faceDetectRectangleArea = (Map<?, ?>) detect.get("faceDectectRectangleArea");
                    double lx = Double.parseDouble(String.valueOf(faceDetectRectangleArea.get("upperLeftX")));
                    double ly = Double.parseDouble(String.valueOf(faceDetectRectangleArea.get("upperLeftY")));
                    double rx = Double.parseDouble(String.valueOf(faceDetectRectangleArea.get("lowerRightX")));
                    double ry = Double.parseDouble(String.valueOf(faceDetectRectangleArea.get("lowerRightY")));
                    //对该位置人脸进行切割
                    String faceName = userId + "_" + UUID.randomUUID().toString().replaceAll("-", "").substring(0, 10) + "_face.png";
                    String faceAddrPath = faceAddr + faceName;
                    // OpenCVUtil.findAndCutFace(photoAddrPath, faceAddrPath);
                    try {
                        OpenCVUtil.cutPhotoFace(lx, ly, rx, ry, photoAddrPath, faceAddrPath);
                        //positions.add(faceAddrPath);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    FaceClustering faceClustering = new FaceClustering();
                    faceClustering.setPosition(imageUrl + faceName);
                    faceClustering.setPhotoId(photo.getId());
                    faceClustering.setUserId(userId);
                    if (faceClusteringList.size() > 0) {
                        Map<?, ?> result = faceService.searchFace(faceAddrPath, Integer.parseInt(airSetId));
                        if (Double.parseDouble(String.valueOf(result.get("confidence"))) > 0.6) {
                            int faceId = Double.valueOf(String.valueOf(result.get("faceId"))).intValue();
                            Long clusteringId = faceService.findFaceClusteringByFaceId(faceId).getClusteringId();
                            faceClustering.setClusteringId(clusteringId);
                            String newFaceId = faceService.addFaceToAirSet(Integer.parseInt(airSetId), faceAddrPath, faceName);
                            Integer airFaceId = Double.valueOf(newFaceId).intValue();
                            faceClustering.setAirFaceId(String.valueOf(airFaceId));
                        } else {
                            // 置信度不够 -> 设置为新的
                            String faceId = faceService.addFaceToAirSet(Integer.parseInt(airSetId), faceAddrPath, faceName);
                            Integer airFaceId = Double.valueOf(faceId).intValue();
                            faceClustering.setAirFaceId(String.valueOf(airFaceId));
                            Clustering clustering = new Clustering();
                            clustering.setUserId(userId);
                            clusteringRepository.save(clustering);
                            faceClustering.setClusteringId(clustering.getId());
                        }
                    } else {
                        String faceId = faceService.addFaceToAirSet(Integer.parseInt(airSetId), faceAddrPath, faceName);
                        Integer airFaceId = Double.valueOf(faceId).intValue();
                        faceClustering.setAirFaceId(String.valueOf(airFaceId));
                        Clustering clustering = new Clustering();
                        clustering.setUserId(userId);
                        clusteringRepository.save(clustering);
                        faceClustering.setClusteringId(clustering.getId());
                    }
                    faceClusteringRepository.save(faceClustering);
                });
//                positions.forEach(position -> {
//                    FaceClustering faceClustering = new FaceClustering();
//                    faceClustering.setPosition(position);
//                    faceClustering.setPhotoId(photo.getId());
//                    faceClustering.setUserId(userId);
//                    if (faceClusteringList.size() > 0) {
//                        List<FaceCompareConfidenceAndClusterIdBo> score = new ArrayList<>();
//                        faceClusteringList.forEach(face -> {
//                                    long clusteringId = face.getClusteringId();
//                                    FaceCompareConfidenceAndClusterIdBo faceCompareConfidenceAndClusterIdBo = new FaceCompareConfidenceAndClusterIdBo();
//                                    faceCompareConfidenceAndClusterIdBo.setClusterId(clusteringId);
//                                    faceCompareConfidenceAndClusterIdBo.setFaceCompareConfidence(faceService.compareFace(position, face.getPosition()));
//                                    score.add(faceCompareConfidenceAndClusterIdBo);
//                                }
//                        );
//                        score.sort(Comparator.comparing(FaceCompareConfidenceAndClusterIdBo::getFaceCompareConfidence).thenComparing(FaceCompareConfidenceAndClusterIdBo::getClusterId));
//                        if (score.get(score.size() - 1).getFaceCompareConfidence() < 0.6) {
//                            Clustering clustering = new Clustering();
//                            clustering.setUserId(userId);
//                            clusteringRepository.save(clustering);
//                            faceClustering.setClusteringId(clustering.getId());
//                        } else {
//                            faceClustering.setClusteringId(score.get(score.size() - 1).getClusterId());
//                        }
//                    } else {
//                        Clustering clustering = new Clustering();
//                        clustering.setUserId(userId);
//                        clusteringRepository.save(clustering);
//                        faceClustering.setClusteringId(clustering.getId());
//                    }
//                    faceClusteringRepository.save(faceClustering);
//                });

            } else if (faceNum > 10) {
                photo.setType("群像");
                photo.setFaceScore(String.valueOf(faceNum * 10));
                photo.setPosition(photoSavePosition);
                photo.setUserId(userId);
                photo.setColorScore(colorScore);
                photoRepository.save(photo);
            } else {
                String type = getPhotoType(getPhotoType(userId, image));
                photo.setType(type);
                photo.setFaceScore(String.valueOf(faceNum * 10));
                photo.setPosition(photoSavePosition);
                photo.setUserId(userId);
                photo.setColorScore(colorScore);
                photoRepository.save(photo);
            }
        }

    }

    public String getPhotoType(long userId, String image) throws IOException {
        //企业云账户：请申请
        Credential credential = new Credential(ak, sk);

        //初始化ECloud client,Region 为部署资源池OP网关地址枚举类，可自行扩展
        IECloudClient ecloudClient = new ECloudDefaultClient(credential, Region.POOL_SZ);

        //待定义产品request
        try {
            //通用物品识别
            EngineImageClassifyDetectPostRequest request = new EngineImageClassifyDetectPostRequest();
            //图片base64 ，注意不要包含 {data:image/jpeg;base64,}
            request.setImage(image);
            request.setUserId(String.valueOf(userId));
            //通用物品检测
            EngineImageClassifyDetectResponse response = ecloudClient.call(request);
            if ("OK".equals(response.getState())) {
                //通用物品检测
                List<EngineClassify> body = response.getBody();
                return JacksonUtil.toJson(body).split(":")[1].split("\"")[1];
            }
        } catch (IOException | ECloudServerException | IllegalAccessException e) {
            //todo exception process...
            e.printStackTrace();
            return null;
        }
        return null;
    }

    public List<PhotoDisplayVo> findAllDaysPhotosByUserId(Long userId) {
        List<Photo> photoList = photoRepository.findPhotoByUserId(userId);
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

    public List<PhotoDisplayVo> findOneDaysAllPhotosByUserId(Long userId, String date) {
        List<Photo> photoList = photoRepository.findPhotoByUserIdAndCreateTime(userId, date);
        return getPhotoDisplayList(photoList);
    }

    public List<PhotoDisplayVo> findOneTypeAllPhotosByUserId(String type, Long userId) {
        List<Photo> photoList = photoRepository.findPhotoByTypeAndUserId(type, userId);
        return getPhotoDisplayList(photoList);
    }

    private List<PhotoDisplayVo> getPhotoDisplayList(List<Photo> photoList) {
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

    public List<PhotoDateAndSorce> findAllTimesEightPhotoByUserId(Long userId) {
        List<Photo> photoList = photoRepository.findPhotoByUserId(userId);
        HashSet<String> date = new HashSet<>();
        List<PhotoDateAndSorce> result = new ArrayList<>();
        photoList.forEach(photo -> {
            date.add(photo.getCreateTime().toString().split(" ")[0]);
        });
        date.forEach(s -> {
            PhotoDateAndSorce photoDateAndSorce = new PhotoDateAndSorce();
            photoDateAndSorce.setDate(s);
            List<Photo> photos = new ArrayList<>();
            photoList.forEach(photo -> {
                if (photo.getCreateTime().toString().split(" ")[0].equals(s)) photos.add(photo);
            });
            photoDateAndSorce.setPhotoList(photos);
            result.add(photoDateAndSorce);
        });
        return result;
    }

    public List<HashMap<String, String>> findAllTypeOnePhotoByUserId(Long userId) {
        List<HashMap<String, String>> photoList = new ArrayList<>();
        photoRepository.findAllTypeOnePhotoByUserId(userId).forEach(photo -> {
            HashMap<String, String> oneTypePhoto = new HashMap<>();
            oneTypePhoto.put("type", photo.getType());
            oneTypePhoto.put("position", photo.getPosition());
            photoList.add(oneTypePhoto);
        });
        return photoList;
    }

    private String getPhotoType(String input) {
        String[] typeList = input.split(",");
        String mainType = "其他";
        for (String type : typeList) {
            if (!mainType.equals("其他")) return mainType;
            if (photoTypeRepository.findPhotoTypesByTypeListLike(type).size() > 0) {
                mainType = photoTypeRepository.findPhotoTypesByTypeListLike(type).get(0).getMainType();
            }
        }
        return mainType;
    }
}
