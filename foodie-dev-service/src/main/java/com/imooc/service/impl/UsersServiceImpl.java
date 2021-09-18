package com.imooc.service.impl;

import com.imooc.mapper.UsersMapper;
import com.imooc.pojo.Users;
import com.imooc.pojo.bo.UserBO;
import com.imooc.service.UsersService;
import com.imooc.utils.DateUtil;
import com.imooc.utils.MD5Utils;
import com.imooc.enums.SexEnum;
import org.n3r.idworker.Sid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestParam;
import tk.mybatis.mapper.entity.Example;

import java.util.Date;

@Service
public class UsersServiceImpl implements UsersService {
    @Autowired
    private UsersMapper usersMapper;

    @Autowired
    private Sid sid;

    private static final String FACE_URL = "https://image.baidu.com/search/detail?z=0&word=%E5%AE%A0%E7%89%A9%E5%9B%BE%E7%89%87&hs=0&pn=1&spn=0&di=&pi=227260&tn=baiduimagedetail&is=&ie=utf-8&oe=utf-8&cs=1951548898%2C3927145&os=&simid=&adpicid=0&lpn=0&fr=albumsdetail&fm=&ic=0&sme=&cg=&bdtype=&oriquery=&objurl=https%3A%2F%2Ft7.baidu.com%2Fit%2Fu%3D1951548898%2C3927145%26fm%3D193%26f%3DGIF&fromurl=ipprf_z2C%24qAzdH3FAzdH3Fooo_z%26e3Bejj6_z%26e3Bv54AzdH3Fri5p5AzdH3F89a00cd09%3F7p4_f576vj%3Dkwt17%267p4_4j1t74%3Dt4w2jfjw6vi%26vit1%3Dlad&gsm=0&islist=&querylist=&album_tab=%E5%8A%A8%E7%89%A9&album_id=688";


    @Override
    public boolean queryUserIsExist(@RequestParam String username) {

        Example example = new Example(Users.class);
        Example.Criteria userCriteria = example.createCriteria();
        userCriteria.andEqualTo("username",username);

        Users users = usersMapper.selectOneByExample(example);
        return users == null ? false : true;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public Users createUser(UserBO userBO) {
        String uid = sid.nextShort();

        Users users = new Users();
        users.setId(uid);
        users.setUsername(userBO.getUsername());
        try {
            users.setPassword(MD5Utils.getMD5Str(userBO.getPassword()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        // 默认用户昵称同用户名
        users.setNickname(userBO.getUsername());
        users.setFace(FACE_URL);
        users.setMobile("");
        users.setEmail("");
        users.setSex(SexEnum.secret.type);
        users.setBirthday(DateUtil.stringToDate("1900-01-01"));
        users.setCreatedTime(new Date());
        users.setUpdatedTime(new Date());

        usersMapper.insert(users);

        Users user = usersMapper.selectByPrimaryKey(users.getId());

        return user;
    }
    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public Users queryUserForLogin(String username, String password) {
        Example example = new Example(Users.class);
        Example.Criteria criteria = example.createCriteria();

        criteria.andEqualTo("username",username);
        criteria.andEqualTo("password",password);
        return usersMapper.selectOneByExample(example);
    }
}
