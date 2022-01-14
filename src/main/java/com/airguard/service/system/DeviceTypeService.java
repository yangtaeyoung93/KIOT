package com.airguard.service.system;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.airguard.mapper.main.system.DeviceTypeMapper;
import com.airguard.model.system.DeviceType;
import com.airguard.mapper.readonly.ReadOnlyMapper;

@Service
public class DeviceTypeService {

  @Autowired
  DeviceTypeMapper categoryMapper;

  @Autowired
  ReadOnlyMapper readOnlyMapper;

  public List<DeviceType> selectCategoryList(String useYn) {
    return readOnlyMapper.selectCategoryList(useYn);
  }

  public void categorySave(DeviceType category) {
    categoryMapper.insertCategory(category);
  }

  public void categoryUpdate(DeviceType category) {
    categoryMapper.updateCategory(category);
  }

  public void categoryDelete(String idx) {
    categoryMapper.deleteCategory(idx);
  }
}
