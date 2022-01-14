package com.airguard.service.common;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.airguard.mapper.main.common.AdminMapper;
import com.airguard.model.common.Admin;
import com.airguard.model.common.Search;
import com.airguard.mapper.readonly.ReadOnlyMapper;
import com.airguard.util.Sha256EncryptUtil;

@Service
public class AdminService {

  @Autowired
  AdminMapper mapper;

  @Autowired
  ReadOnlyMapper readOnlyMapper;

  public Admin findAdminByLoginId(String loginId) {
    return readOnlyMapper.findAdminByLoginId(loginId);
  }

  public Long findAdminIdx(String adminId) {
    return readOnlyMapper.findAdminIdx(adminId);
  }

  public int loginCheckAdminId(Admin admin) {
    return readOnlyMapper.loginCheckAdminId(admin);
  }

  public void adminLoginInfoUpdate(Admin admin) {
    mapper.adminLoginInfoUpdate(admin);
  }

  public List<Admin> selectAdminList(Search search) {
    return readOnlyMapper.selectAdminList(search);
  }

  public Admin selectAdminOne(String idx) {
    return readOnlyMapper.selectAdminOne(idx);
  }

  public void insertAdmin(Admin admin) {
    String encPwd = Sha256EncryptUtil.ShaEncoder(admin.getUserPw());
    admin.setUserPw(encPwd);
    mapper.insertAdmin(admin);
  }

  public void updateAdmin(Admin admin) {
    String encPwd = Sha256EncryptUtil.ShaEncoder(admin.getUserPw());
    admin.setUserPw(encPwd);
    mapper.updateAdmin(admin);
  }

  public void deleteAdmin(String idx) {
    mapper.deleteAdmin(idx);
  }
}
