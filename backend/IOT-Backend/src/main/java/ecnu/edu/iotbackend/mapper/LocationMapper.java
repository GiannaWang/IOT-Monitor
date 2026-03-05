package ecnu.edu.iotbackend.mapper;

import ecnu.edu.iotbackend.entity.Location;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface LocationMapper {

    // 获取第一个location的id
    @Select("SELECT id FROM locations ORDER BY id LIMIT 1")
    Integer getFirstLocationId();

    // 创建默认location（如果不存在）
    @Insert("INSERT INTO locations (buildingname, floornumber, roomnumber, description) VALUES ('B', 1, 101, '默认位置')")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insertDefaultLocation();

    // 获取所有位置
    @Select("SELECT id, buildingname, floornumber, roomnumber, description FROM locations ORDER BY id")
    List<Location> getAllLocations();

    // 根据ID获取位置
    @Select("SELECT id, buildingname, floornumber, roomnumber, description FROM locations WHERE id = #{id}")
    Location getLocationById(Integer id);
}

