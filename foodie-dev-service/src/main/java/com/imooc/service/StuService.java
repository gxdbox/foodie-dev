package com.imooc.service;

import com.imooc.pojo.Stu;

public interface StuService {
    Stu getInfo(Integer id);
    void  updateStu(Stu stu);
    void insert(Stu stu);
    void  delete(Integer id);
    public void saveParent();
    public void saveChildren();
}
