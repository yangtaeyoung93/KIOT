package com.airguard.service.system;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.airguard.mapper.main.system.MenuMapper;
import com.airguard.model.common.Admin;
import com.airguard.model.system.Menu;
import com.airguard.mapper.readonly.ReadOnlyMapper;

@Service
public class MenuService {

  @Autowired
  MenuMapper mapper;

  @Autowired
  ReadOnlyMapper readOnlyMapper;

  public List<Menu> selectMenuList(String str) {
    return readOnlyMapper.selectMenuList(str);
  }

  public List<Menu> selectHighRankMenuList() {
    return readOnlyMapper.selectHighRankMenuList();
  }

  public List<Menu> selectMenuAuthList(String str) {
    return readOnlyMapper.selectMenuAuthList(str);
  }

  public Menu selectMenuOne(String idx) {
    return readOnlyMapper.selectMenuOne(idx);
  }

  public List<Admin> selectAdminMenuLIst() {
    return readOnlyMapper.selectAdminMenuLIst();
  }

  public List<Menu> selectAdminMenuOne(String adminIdx) {
    return readOnlyMapper.selectAdminMenuOne(adminIdx);
  }

  public void insertMenu(Menu menu) {
    mapper.insertMenu(menu);
  }

  public void updateMenu(Menu menu) {
    mapper.updateMenu(menu);
  }

  public void deleteMenu(String idx) {
    mapper.deleteMenu(idx);
  }

  public void insertMenuAuth(String adminIdx, String menuIdx) {
    mapper.insertMenuAuth(adminIdx, menuIdx);
  }

  public void deleteMenuAuth(String adminIdx, String menuIdx) {
    mapper.deleteMenuAuth(adminIdx, menuIdx);
  }
}
