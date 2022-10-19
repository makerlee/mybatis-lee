package mybatis.mapper;

import mybatis.po.User;

public interface IUserMapper {
    User queryUserInfoById(Integer uId);

    User queryUserInfo(User user);

    int updateUserInfo(User user);

    int deleteUserInfoByUserId(String userId);

    void insertUserInfo(User user);
}
