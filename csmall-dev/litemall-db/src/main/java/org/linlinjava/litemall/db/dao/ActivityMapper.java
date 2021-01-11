package org.linlinjava.litemall.db.dao;

import org.apache.ibatis.annotations.Param;
import org.linlinjava.litemall.db.domain.LitemallActivity;

import java.util.List;

public interface ActivityMapper {
    List<LitemallActivity> list();

    LitemallActivity getOne(@Param("id") Integer id);

    void update(@Param("activity") LitemallActivity activity);

}
