package cn.tp.controllers;

import cn.tp.services.FaceService;
import cn.tp.services.PhotoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api/face")
public class FaceController {
    private final FaceService faceService;

    @Autowired
    public FaceController(FaceService faceService) {
        this.faceService = faceService;
    }

    @GetMapping(value = "/all/one/{userId}")
    public ResponseEntity<?> getAllFaceOnePhoto(@PathVariable Long userId) {
        return ResponseEntity.ok(faceService.getAllClusteringOneFaceByUserId(userId));
    }

    @GetMapping(value = "/eight/one/{userId}")
    public ResponseEntity<?> getEightFaceOnePhoto(@PathVariable Long userId) {
        return ResponseEntity.ok(faceService.getEightClusteringOneFaceByUserId(userId));
    }

    @GetMapping(value = "/oneKlass/all/{userId}")
    public ResponseEntity<?> getOneKlassAllPhoto(@PathVariable Long userId, @PathVariable Long clusteringId) {
        return ResponseEntity.ok(faceService.findOneKlassAllPhotoByUserIdAndClusteringId(userId, clusteringId));
    }
}
