package ecnu.edu.iotbackend.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface LocationMapper {
    
    // 获取第一个location的id
    @Select("SELECT id FROM locations ORDER BY id LIMIT 1")
    Integer getFirstLocationId();
    
    // 创建默认location（如果不存在）
    @Insert("INSERT INTO locations (buildingname, floornumber, roomnumber, description) VALUES ('B', 1, 101, '默认位置')")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insertDefaultLocation();
}

