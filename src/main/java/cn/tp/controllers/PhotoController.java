package cn.tp.controllers;

import cn.tp.entities.bo.UserUploadPhotoBo;
import cn.tp.services.PhotoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;


@RestController
@RequestMapping(value = "/api/photo")
public class PhotoController {

    private final PhotoService photoService;

    @Autowired
    public PhotoController(PhotoService photoService) {
        this.photoService = photoService;
    }

    @PostMapping(value = "/upload")
    public ResponseEntity<?> photoUpload(@RequestBody UserUploadPhotoBo userUploadPhotoBo) throws Exception {
        photoService.savePhotoToFile(userUploadPhotoBo);
        return null;
    }

    @GetMapping(value = "/days/all/{userId}")
    public ResponseEntity<?> getAllPhoto(@PathVariable Long userId) {
        return ResponseEntity.ok(photoService.findAllDaysPhotosByUserId(userId));
    }

    @GetMapping(value = "/day/{date}/all/{userId}")
    public ResponseEntity<?> getOneDayAllPhoto(@PathVariable Date date, @PathVariable Long userId) {
        return ResponseEntity.ok(photoService.findOneDaysAllPhotosByUserId(userId, date));
    }

    @GetMapping(value = "/type/{type}/{userId}")
    public ResponseEntity<?> getOneTypeAllPhoto(@PathVariable String type, @PathVariable Long userId) {
        return ResponseEntity.ok(photoService.findOneTypeAllPhotosByUserId(type, userId));
    }

    @GetMapping(value = "/all/eight/{userId}")
    public ResponseEntity<?> getOneDayEightPhoto(@PathVariable Long userId) {
        return ResponseEntity.ok(photoService.findAllTimesEightPhotoByUserId(userId));
    }

    @GetMapping(value = "/all/type/one/{userId}")
    public ResponseEntity<?> getAllTypeOnePhoto(@PathVariable Long userId) {
        return ResponseEntity.ok(photoService.findAllTypeOnePhotoByUserId(userId));
    }
}
