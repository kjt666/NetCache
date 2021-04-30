package wowo.kjt.netcachedemo;

import android.os.Parcel;
import android.os.Parcelable;


import java.util.List;

/**
 * Created by kjt on 2020/5/11
 */
public class StudyHistoryListBean implements Parcelable {


    /**
     * title : 最近学习
     * list : [{"title":"章节名称","image":"http://img.youshu.cc/readoo/20181129/5bff8ebc5d6c4.png","image_icon":"","sub_text":"书籍名称","study_time":"1582732800"},{"title":"章节名称2","image":"http://img.youshu.cc/readoo/20181129/5bff8ebc5d6c4.png","image_icon":"","sub_text":"书籍名称","study_time":"1582732800"}]
     */

    public String title;
    public List<StudyHistoryBean> list;

    public static class StudyHistoryBean implements Parcelable {
        /**
         * title : 章节名称
         * image : http://img.youshu.cc/readoo/20181129/5bff8ebc5d6c4.png
         * image_icon :
         * sub_text : 书籍名称
         * study_time : 1582732800
         * interval_day ： 0 - 今天 1-昨天, 3-更早
         * scheme
         * label
         * action_log
         */

        public String title;
        public String image;
        public String image_icon;
        public String sub_text;
        public String study_time;
        public String interval_day;
        public String scheme;
        public String label;//自己加的标志，标记今天昨天和更早

        public StudyHistoryBean() {
        }


        protected StudyHistoryBean(Parcel in) {
            title = in.readString();
            image = in.readString();
            image_icon = in.readString();
            sub_text = in.readString();
            study_time = in.readString();
            interval_day = in.readString();
            scheme = in.readString();
            label = in.readString();
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(title);
            dest.writeString(image);
            dest.writeString(image_icon);
            dest.writeString(sub_text);
            dest.writeString(study_time);
            dest.writeString(interval_day);
            dest.writeString(scheme);
            dest.writeString(label);
        }

        @Override
        public int describeContents() {
            return 0;
        }

        public static final Creator<StudyHistoryBean> CREATOR = new Creator<StudyHistoryBean>() {
            @Override
            public StudyHistoryBean createFromParcel(Parcel in) {
                return new StudyHistoryBean(in);
            }

            @Override
            public StudyHistoryBean[] newArray(int size) {
                return new StudyHistoryBean[size];
            }
        };

        @Override
        public String toString() {
            return "StudyHistoryBean{" +
                    "title='" + title + '\'' +
                    ", image='" + image + '\'' +
                    ", image_icon='" + image_icon + '\'' +
                    ", sub_text='" + sub_text + '\'' +
                    ", study_time='" + study_time + '\'' +
                    ", interval_day='" + interval_day + '\'' +
                    ", scheme='" + scheme + '\'' +
                    ", label='" + label + '\'' +
                    '}';
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.title);
        dest.writeTypedList(this.list);
    }

    public StudyHistoryListBean() {
    }

    protected StudyHistoryListBean(Parcel in) {
        this.title = in.readString();
        this.list = in.createTypedArrayList(StudyHistoryBean.CREATOR);
    }

    public static final Parcelable.Creator<StudyHistoryListBean> CREATOR = new Parcelable.Creator<StudyHistoryListBean>() {
        @Override
        public StudyHistoryListBean createFromParcel(Parcel source) {
            return new StudyHistoryListBean(source);
        }

        @Override
        public StudyHistoryListBean[] newArray(int size) {
            return new StudyHistoryListBean[size];
        }
    };

    @Override
    public String toString() {
        return "StudyHistoryListBean{" +
                "title='" + title + '\'' +
                ", list=" + list +
                '}';
    }
}
