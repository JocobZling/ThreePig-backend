package cn.tp.services;

import cn.tp.entities.Photo;
import cn.tp.repositories.PhotoRepository;
import cn.tp.utils.SortUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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
        List<Photo> resultPhoto = photoRepository.findPhotoByUserId(userId);
        //调用排序
        List<Photo> SortList = SortUtil.SortPhoto(resultPhoto,flag);

        List<String> result = new ArrayList<String>();
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
        //
        List<Photo> resultPhoto = photoRepository.findPhotoByUserId(userId);
        //拿到最近num 天的数据 不够num全拿来
        List<Photo> RencntList = SortUtil.GetNumPhotoBaseTime(resultPhoto,num);
        //根据分数排序
        List<Photo> SortRecentList = SortUtil.SortPhoto(RencntList,flag);

        List<String> result = new ArrayList<String>();
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

    public List<Photo> test(Long userId)
    {
        return photoRepository.findPhotoByUserId(userId);
    }

}
