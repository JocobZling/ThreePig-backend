package cn.tp.utils;

import cn.tp.entities.Photo;
import lombok.*;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.function.DoubleBinaryOperator;

//给Photo排序
@Setter
@Getter
@NoArgsConstructor
@Builder
public class SortUtil {
    public static List<Photo> SortPhoto(List<Photo> resultPhoto, boolean flag)
    {
        if(flag)
        {
            return SortPersonPhoto(resultPhoto);
        }
        else
        {
            return SortAllPhoto(resultPhoto);
        }
    }

    private static List<Photo> SortPersonPhoto(List<Photo> resultPhoto)
    {
        Collections.sort(resultPhoto, new Comparator<Photo>() {
            @Override
            public int compare(Photo o1, Photo o2) {
                String PersonScore1 = o1.getFaceScore();
                String PersonScore2 = o2.getFaceScore();

                Double Score1 = Double.parseDouble(PersonScore1);
                Double Score2 = Double.parseDouble(PersonScore2);

                Double diff = Score2 - Score1;

                if(diff > 0)
                {
                    return 1;
                }
                else if(diff < 0)
                {
                    return -1;
                }
                return 0;
            }
        });

        return resultPhoto;

    }

    private static List<Photo> SortAllPhoto(List<Photo> resultPhoto)
    {
        Collections.sort(resultPhoto, new Comparator<Photo>() {
            @Override
            public int compare(Photo o1, Photo o2) {
                String PersonScore1 = o1.getFaceScore();
                String PersonScore2 = o2.getFaceScore();

                String ColorScore1 = o1.getColorScore();
                String ColorScore2 = o2.getColorScore();


                Double Score1 = Double.parseDouble(PersonScore1) + Double.parseDouble(ColorScore1);
                Double Score2 = Double.parseDouble(PersonScore2) + Double.parseDouble(ColorScore2);

                Double diff = Score2 - Score1;

                if(diff > 0)
                {
                    return 1;
                }
                else if(diff < 0)
                {
                    return -1;
                }
                return 0;
            }
        });

        return resultPhoto;

    }

}
