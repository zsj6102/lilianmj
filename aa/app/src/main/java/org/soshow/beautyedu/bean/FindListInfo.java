package org.soshow.beautyedu.bean;

import java.util.ArrayList;
import java.util.List;
/** 圈子动态 Dynamic */
public class FindListInfo {
    /**动态id  == dynamic_id */
    private Integer id;
    private Integer goods_num;
    private String content;
    private Integer comment_num;
    private String nickname;
    private String user_id;
    private String create_time;
    private String create_time_pre;
    private String photos;
    public String photos_url;
    private String is_liked;
    private Integer status;
    public List<CommentsEntity> comments = new ArrayList<>();

    private String goods;
    private String is_recommend;
    private String is_works;
    private String goods_id;
    private String cell_phone;
    private String real_name;
    private String gender;
    private String user_photo_url;

    public String getUser_photo_url() {
        return user_photo_url;
    }

    public void setUser_photo_url(String user_photo_url) {
        this.user_photo_url = user_photo_url;
    }
    //    @Deprecated
//    private List<PicsEntity> pics = new ArrayList<>();


    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
    
    /// gett sett   ------------------------------------------------------------------------


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getPhotos() {
        return photos;
    }

    public void setPhotos(String photos) {
        this.photos = photos;
    }

    public Integer getComment_num() {
        return comment_num;
    }

    public void setComment_num(Integer comment_num) {
        this.comment_num = comment_num;
    }
/// gett sett

    public List<CommentsEntity> getComments() {
        return comments;
    }
    public void setComments(List<CommentsEntity> comments) {
        this.comments = comments;
    }

    public String getCreate_time_pre() {
        return create_time_pre;
    }

    public void setCreate_time_pre(String create_time_pre) {
        this.create_time_pre = create_time_pre;
    }

    public Integer getGoods_num() {
        return goods_num;
    }

    public void setGoods_num(Integer goods_num) {
        this.goods_num = goods_num;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public void setGoods(String goods) {
        this.goods = goods;
    }

    public void setCreate_time(String create_time) {
        this.create_time = create_time;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setIs_recommend(String is_recommend) {
        this.is_recommend = is_recommend;
    }

    public void setIs_works(String is_works) {
        this.is_works = is_works;
    }

    public void setGoods_id(String goods_id) {
        this.goods_id = goods_id;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public void setCell_phone(String cell_phone) {
        this.cell_phone = cell_phone;
    }

    public void setReal_name(String real_name) {
        this.real_name = real_name;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public void setPhotos_url(String photos_url) {
        this.photos_url = photos_url;
    }

    public void setIs_liked(String is_liked) {
        this.is_liked = is_liked;
    }

//    public void setPics(List<PicsEntity> pics) {
//        this.pics = pics;
//    }

    public String getUser_id() {
        return user_id;
    }

    public String getGoods() {
        return goods;
    }

    public String getCreate_time() {
        return create_time;
    }

    public String getContent() {
        return content;
    }

    public String getIs_recommend() {
        return is_recommend;
    }

    public String getIs_works() {
        return is_works;
    }

    public String getGoods_id() {
        return goods_id;
    }

    public String getNickname() {
        return nickname;
    }

    public String getCell_phone() {
        return cell_phone;
    }

    public String getReal_name() {
        return real_name;
    }

    public String getGender() {
        return gender;
    }

    public String getPhotos_url() {
        return photos_url;
    }

    public String getIs_liked() {
        return is_liked;
    }

//    public List<PicsEntity> getPics() {
//        return pics;
//    }

    public static class CommentsEntity extends  CommentInfo {
        public String replyid = "0";
        @Deprecated //TODO notuse
        public ReplyEntity reply = null;
        @Deprecated
        public String getReplyid() {
            return replyid;
        }

        public void setReplyid(String replyid) {
            this.replyid = replyid;
        }

        public ReplyEntity getReply() {
            return reply;
        }

        public void setReply(ReplyEntity reply) {
            this.reply = reply;
        }


        public static class ReplyEntity {
            private String comment_id;
            private String dynamic_id;
            private String user_id;
            private String comment;
            private String addtime;
            private String replyid;
            private String cell_phone;
            private String username;
            private String real_name;

            public void setComment_id(String comment_id) {
                this.comment_id = comment_id;
            }

            public void setDynamic_id(String dynamic_id) {
                this.dynamic_id = dynamic_id;
            }

            public void setUser_id(String user_id) {
                this.user_id = user_id;
            }

            public void setComment(String comment) {
                this.comment = comment;
            }

            public void setAddtime(String addtime) {
                this.addtime = addtime;
            }

            public void setReplyid(String replyid) {
                this.replyid = replyid;
            }

            public void setCell_phone(String cell_phone) {
                this.cell_phone = cell_phone;
            }

            public void setUsername(String username) {
                this.username = username;
            }

            public void setReal_name(String real_name) {
                this.real_name = real_name;
            }

            public String getComment_id() {
                return comment_id;
            }

            public String getDynamic_id() {
                return dynamic_id;
            }

            public String getUser_id() {
                return user_id;
            }

            public String getComment() {
                return comment;
            }

            public String getAddtime() {
                return addtime;
            }

            public String getReplyid() {
                return replyid;
            }

            public String getCell_phone() {
                return cell_phone;
            }

            public String getUsername() {
                return username;
            }

            public String getReal_name() {
                return real_name;
            }
        }


    }

    public static class PicsEntity {
        private String pic_id;
        private String dynamic_id;
        private String pic_url;

        public void setPic_id(String pic_id) {
            this.pic_id = pic_id;
        }

        public void setDynamic_id(String dynamic_id) {
            this.dynamic_id = dynamic_id;
        }

        public void setPic_url(String pic_url) {
            this.pic_url = pic_url;
        }

        public String getPic_id() {
            return pic_id;
        }

        public String getDynamic_id() {
            return dynamic_id;
        }

        public String getPic_url() {
            return pic_url;
        }
    }
}
