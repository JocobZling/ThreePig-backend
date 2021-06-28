package cn.tp.controllers;

import cn.tp.services.PhotoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api/highlight")
public class HighlightsController {

    private final PhotoService photoService;

    @Autowired
    public HighlightsController(PhotoService photoService) {
        this.photoService = photoService;
    }

    //封面一张 所有
    @GetMapping(value="/{userId}")
    public ResponseEntity<?> getHighlightPageContent(@PathVariable Long userId){
        return ResponseEntity.ok(123);
    }

    //
    @GetMapping(value = "/everyTimes/person/{userId}")
    public ResponseEntity<?> getEveryTimesPersonHighlight(@PathVariable Long userId) {
        return ResponseEntity.ok(123);
    }

    @GetMapping(value = "/everyTimes/all/{userId}")
    public ResponseEntity<?> getEveryTimesAllHighlight(@PathVariable Long userId) {
        return ResponseEntity.ok(123);
    }

    @GetMapping(value = "/top/person/{userId}")
    public ResponseEntity<?> getTopPersonHighlight(@PathVariable Long userId) {
        return ResponseEntity.ok(123);
    }

    @GetMapping(value = "/top/all/{userId}")
    public ResponseEntity<?> getTopAllHighlight(@PathVariable Long userId) {
        return ResponseEntity.ok(123);
    }

    @GetMapping(value = "/recent/person/{userId}")
    public ResponseEntity<?> getRecentPersonHighlight() {
        return ResponseEntity.ok(123);
    }

    @GetMapping(value = "/recent/all/{userId}")
    public ResponseEntity<?> getRecentAllHighlight() {
        return ResponseEntity.ok(123);
    }

}
