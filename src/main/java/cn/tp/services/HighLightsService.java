package cn.tp.services;

import cn.tp.entities.Photo;
import cn.tp.repositories.PhotoRepository;
import cn.tp.utils.SortUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

@Service
public class HighLightsService {
    private final PhotoRepository photoRepository;

    @Autowired
    public HighLightsService(PhotoRepository photoRepository) {
        this.photoRepository = photoRepository;
    }


    public List<String> getTopPerson(Long userId, int num,boolean flag)
    {
        List<String> result = new ArrayList<String>();
        List<Photo> resultPhoto = photoRepository.findPhotoByUserId(userId);
        if(resultPhoto.size() == 0)
        {
            return result;
        }
        //调用排序
        List<Photo> SortList = SortUtil.SortPhoto(resultPhoto,flag);

        int len = SortList.size();
        if(len > num)
        {
            for(int i = 0;i<num;i++)
            {
                Photo tmp = SortList.get(i);
                result.add(tmp.getPosition());
            }

        }
        else
        {
            for(Photo tmp : SortList)
            {
                result.add(tmp.getPosition());
            }
        }
        return result;

    }

    // num是指定拿出最近几天的数据 flag是指定facescore排序还是allscore排序 photoNum是指定最后取出多少张
    public List<String> getRecentTop(Long userId, int num,boolean flag,int photoNum)
    {
        List<String> result = new ArrayList<String>();
        List<Photo> resultPhoto = photoRepository.findPhotoByUserId(userId);
        //拿到最近num 天的数据 不够num全拿来
        List<Photo> RencntList = SortUtil.GetNumPhotoBaseTime(resultPhoto,num);
        if(RencntList.size() == 0)
        {
            return result;
        }
        //根据分数排序
        List<Photo> SortRecentList = SortUtil.SortPhoto(RencntList,flag);

        int len = SortRecentList.size();
        if(len > photoNum)
        {
            for(int i = 0;i<photoNum;i++)
            {
                Photo tmp = SortRecentList.get(i);
                result.add(tmp.getPosition());
            }

        }
        else
        {
            for(Photo tmp : SortRecentList)
            {
                result.add(tmp.getPosition());
            }
        }
        return result;


    }

    public List<String> getTimeTop(long userId, String time, int num)
    {
        List<String> result = new ArrayList<String>();
        List<Photo> resultPhoto = photoRepository.findPhotoByUserId(userId);
        List<Photo> timePhoto = SortUtil.GetTimePhoto(resultPhoto,time);
        if(timePhoto.size() == 0)
        {
            return result;
        }
        boolean flag = false;

        List<Photo> sortTimePhoto = SortUtil.SortPhoto(timePhoto,flag);
        int len = sortTimePhoto.size();
        if(len > num)
        {
            for(int i = 0;i<num;i++)
            {
                Photo tmp = sortTimePhoto.get(i);
                result.add(tmp.getPosition());
            }

        }
        else
        {
            for(Photo tmp : sortTimePhoto)
            {
                result.add(tmp.getPosition());
            }
        }
        return result;
    }

    public List<HashMap<String,String>> getEverytimePhoto(long userId) {

        List<HashMap<String, String>> res = new ArrayList<HashMap<String, String>>();

        List<Photo> resultPhoto = photoRepository.findPhotoByUserId(userId);
        if(resultPhoto.size() == 0)
        {
            return res;
        }
        List<Photo> sortTimePhoto = SortUtil.SortTime(resultPhoto);

        List<List<Photo>> everyTimePhoto = SortUtil.GetEveryTimePhoto(sortTimePhoto);

        for (List<Photo> oneTimePhoto : everyTimePhoto)
        {
            HashMap<String, String> TimePosition = new HashMap<String, String>();
            List<Photo> tmp = SortUtil.SortPhoto(oneTimePhoto,false);
            TimePosition.put(SortUtil.DateToStringDay(tmp.get(0).getCreateTime()),tmp.get(0).getPosition());
            res.add(TimePosition);
        }
        return res;

    }

    public String BsetPerson(long userId)
    {
        List<String> res = getTopPerson(userId,1,true);
        return res.get(0);
    }

    public String BsetAll(long userId)
    {
        List<String> res = getTopPerson(userId,1,false);
        return res.get(0);
    }

    public String BestRecentPerson(long userId)
    {
        List<String> res = getRecentTop(userId,5,true,1);
        return res.get(0);
    }

    public String BestRecentAll(long userId)
    {
        List<String> res = getRecentTop(userId,5,false,1);
        return res.get(0);
    }



    public List<Photo> test(Long userId)
    {
        return photoRepository.findPhotoByUserId(userId);
    }

}
