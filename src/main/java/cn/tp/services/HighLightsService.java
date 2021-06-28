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

    public List<Photo> test(Long userId)
    {
        return photoRepository.findPhotoByUserId(userId);
    }

}
