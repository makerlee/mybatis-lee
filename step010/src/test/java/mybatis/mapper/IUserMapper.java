package mybatis.mapper;

import mybatis.po.User;

public interface IUserMapper {
    User queryUserInfoById(Integer uId);

    User queryUserInfo(User user);
}
