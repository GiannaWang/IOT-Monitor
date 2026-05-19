package ecnu.edu.iotbackend.mapper;

import ecnu.edu.iotbackend.entity.Location;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Delete;

import java.util.List;

@Mapper
public interface LocationMapper {

    // 获取第一个location的id
    @Select("SELECT id FROM locations ORDER BY id LIMIT 1")
    Integer getFirstLocationId();

    @Select("SELECT COUNT(*) FROM locations")
    int countLocations();

    // 创建默认location（如果不存在）
    @Insert("INSERT INTO locations (buildingname, floornumber, roomnumber, description) VALUES ('B', 1, 101, '默认位置')")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insertDefaultLocation();

    @Insert("INSERT INTO locations (buildingname, floornumber, roomnumber, description) VALUES (#{buildingname}, #{floornumber}, #{roomnumber}, #{description})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insertLocation(Location location);

    @Delete("DELETE FROM locations WHERE id = #{id}")
    int deleteLocationById(Integer id);

    // 获取所有位置
    @Select("SELECT id, buildingname, floornumber, roomnumber, description FROM locations ORDER BY id")
    List<Location> getAllLocations();

    // 根据ID获取位置
    @Select("SELECT id, buildingname, floornumber, roomnumber, description FROM locations WHERE id = #{id}")
    Location getLocationById(Integer id);

    @Select("SELECT COUNT(*) FROM locations WHERE buildingname = #{buildingname} AND floornumber = #{floornumber} AND roomnumber = #{roomnumber}")
    int countByRoomIdentity(@Param("buildingname") String buildingname,
                            @Param("floornumber") Integer floornumber,
                            @Param("roomnumber") Integer roomnumber);

    @Select({
            "<script>",
            "SELECT id, buildingname, floornumber, roomnumber, description FROM locations",
            "WHERE id IN",
            "<foreach collection='ids' item='id' open='(' separator=',' close=')'>",
            "#{id}",
            "</foreach>",
            "ORDER BY id",
            "</script>"
    })
    List<Location> getLocationsByIds(@Param("ids") List<Integer> ids);
}

