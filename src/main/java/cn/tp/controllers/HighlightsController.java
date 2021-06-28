package cn.tp.controllers;

import cn.tp.entities.Photo;
import cn.tp.services.HighLightsService;
import cn.tp.services.PhotoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping(value = "/api/highlight")
public class HighlightsController {

    private final PhotoService photoService;
    private final HighLightsService highLightsService;

    @Autowired
    public HighlightsController(PhotoService photoService,HighLightsService highLightsService) {
        this.photoService = photoService;
        this.highLightsService = highLightsService;
    }

    //封面一张 所有
    @GetMapping(value="/{userId}")
    public ResponseEntity<?> getHighlightPageContent(@PathVariable Long userId){
        return ResponseEntity.ok(123);
    }

    //
//    @GetMapping(value = "/everyTimes/person/{userId}/")
//    public ResponseEntity<?> getEveryTimesPersonHighlight(@PathVariable Long userId) {
//        return ResponseEntity.ok(123);
//    }

    //拿出用户每天的所有的top10
    @GetMapping(value = "/everyTimes/{createTime}/all/{userId}")
    public ResponseEntity<?> getEveryTimesAllHighlight(@PathVariable Long userId,@PathVariable String time) {
        return ResponseEntity.ok(123);
    }

    //拿出用户所有的top person_score 10
    @GetMapping(value = "/top/person/{userId}")
    public ResponseEntity<?> getTopPersonHighlight(@PathVariable Long userId) {
        // 根据userid取出这个所有的position以及对应的faceScore 根据faceScore排序
        // 然后给出position

        //需要取出的数量
        int num = 10;
        //flag = true 代表只按照faceScore排序
        boolean flag = true;
        List<String> result = highLightsService.getTopPerson(userId,num,flag);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    //拿出用户所有的top 所有的 10
    @GetMapping(value = "/top/all/{userId}")
    public ResponseEntity<?> getTopAllHighlight(@PathVariable Long userId) {
        // 根据userid取出这个所有的position以及对应的总分数 根据总分数排序
        // 然后给出position

        //需要取出的数量
        int num = 10;
        //flag = false 代表按照总分数排序
        boolean flag = false;
        List<String> result = highLightsService.getTopPerson(userId,num,flag);
        return new ResponseEntity<>(result, HttpStatus.OK);

    }

    //拿出用户最近的五天top person_scores 20
    @GetMapping(value = "/recent/person/{userId}")
    public ResponseEntity<?> getRecentPersonHighlight(@PathVariable Long userId) {
        int num = 5; //最近几天
        int photoNum = 20; //图片数量
        //flag = true 代表只按照faceScore排序
        boolean flag = true;
        List<String> result = highLightsService.getRecentTop(userId,num,flag,photoNum);

        return new ResponseEntity<>(result,HttpStatus.OK);
    }

    //拿出用户最近的五天top all_scores top 20
    @GetMapping(value = "/recent/all/{userId}")
    public ResponseEntity<?> getRecentAllHighlight(@PathVariable Long userId) {
        int num = 5; //最近几天
        int photoNum = 20; //图片数量
        //flag = flase 代表只按照所有的分数排序
        boolean flag = false;
        List<String> result = highLightsService.getRecentTop(userId,num,flag,photoNum);

        return new ResponseEntity<>(result,HttpStatus.OK);

    }

    @GetMapping(value="test/{userId}")
    public ResponseEntity<?> test(@PathVariable Long userId) throws ParseException {
        List<Photo> result = highLightsService.test(userId);
        Date createTime1 = result.get(0).getCreateTime();
        Date createTime2 = result.get(2).getCreateTime();
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        String startDateStr=df.format(createTime1);
        Date startDate=df.parse(startDateStr);
        System.out.println(startDate);
        System.out.println(startDateStr);
        System.out.println(createTime2);
        return new ResponseEntity<>(result,HttpStatus.OK);
    }

}
