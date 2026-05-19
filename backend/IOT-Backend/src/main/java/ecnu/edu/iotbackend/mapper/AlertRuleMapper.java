package ecnu.edu.iotbackend.mapper;

import ecnu.edu.iotbackend.entity.AlertRule;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface AlertRuleMapper {

    String RULE_COLUMNS = "ar.id, ar.rulename, ar.description, ar.rulecondition, ar.sensortype, ar.severity, " +
            "ar.locationid, ar.enabled, ar.createdbyuserid, ar.createtime, ar.updatetime";

    @Select("SELECT " + RULE_COLUMNS + ", " +
            "CASE WHEN ar.locationid IS NULL THEN 'Global Rule' ELSE CONCAT(l.buildingname, '-', l.floornumber, 'F-', l.roomnumber) END AS locationname " +
            "FROM alert_rules ar LEFT JOIN locations l ON l.id = ar.locationid ORDER BY ar.locationid IS NULL DESC, ar.locationid, ar.sensortype")
    @Results({
            @Result(column = "rulename", property = "ruleName"),
            @Result(column = "rulecondition", property = "ruleCondition"),
            @Result(column = "sensortype", property = "sensorType"),
            @Result(column = "locationid", property = "locationId"),
            @Result(column = "locationname", property = "locationName"),
            @Result(column = "createdbyuserid", property = "createdByUserId"),
            @Result(column = "createtime", property = "createTime"),
            @Result(column = "updatetime", property = "updateTime")
    })
    List<AlertRule> getAllRules();

    @Select("SELECT " + RULE_COLUMNS + ", " +
            "CASE WHEN ar.locationid IS NULL THEN 'Global Rule' ELSE CONCAT(l.buildingname, '-', l.floornumber, 'F-', l.roomnumber) END AS locationname " +
            "FROM alert_rules ar LEFT JOIN locations l ON l.id = ar.locationid " +
            "WHERE ar.enabled = 1 ORDER BY ar.locationid IS NULL DESC, ar.locationid, ar.sensortype")
    @Results({
            @Result(column = "rulename", property = "ruleName"),
            @Result(column = "rulecondition", property = "ruleCondition"),
            @Result(column = "sensortype", property = "sensorType"),
            @Result(column = "locationid", property = "locationId"),
            @Result(column = "locationname", property = "locationName"),
            @Result(column = "createdbyuserid", property = "createdByUserId"),
            @Result(column = "createtime", property = "createTime"),
            @Result(column = "updatetime", property = "updateTime")
    })
    List<AlertRule> getEnabledRules();

    @Select("SELECT " + RULE_COLUMNS + ", " +
            "CASE WHEN ar.locationid IS NULL THEN 'Global Rule' ELSE CONCAT(l.buildingname, '-', l.floornumber, 'F-', l.roomnumber) END AS locationname " +
            "FROM alert_rules ar LEFT JOIN locations l ON l.id = ar.locationid " +
            "ORDER BY ar.locationid IS NULL DESC, ar.locationid, ar.sensortype")
    @Results({
            @Result(column = "rulename", property = "ruleName"),
            @Result(column = "rulecondition", property = "ruleCondition"),
            @Result(column = "sensortype", property = "sensorType"),
            @Result(column = "locationid", property = "locationId"),
            @Result(column = "locationname", property = "locationName"),
            @Result(column = "createdbyuserid", property = "createdByUserId"),
            @Result(column = "createtime", property = "createTime"),
            @Result(column = "updatetime", property = "updateTime")
    })
    List<AlertRule> getAllRulesForManagement();

    @Select("SELECT " + RULE_COLUMNS + ", 'Global Rule' AS locationname " +
            "FROM alert_rules ar WHERE ar.locationid IS NULL ORDER BY ar.sensortype")
    @Results({
            @Result(column = "rulename", property = "ruleName"),
            @Result(column = "rulecondition", property = "ruleCondition"),
            @Result(column = "sensortype", property = "sensorType"),
            @Result(column = "locationid", property = "locationId"),
            @Result(column = "locationname", property = "locationName"),
            @Result(column = "createdbyuserid", property = "createdByUserId"),
            @Result(column = "createtime", property = "createTime"),
            @Result(column = "updatetime", property = "updateTime")
    })
    List<AlertRule> getGlobalRulesForManagement();

    @Select({
            "<script>",
            "SELECT " + RULE_COLUMNS + ",",
            "CASE WHEN ar.locationid IS NULL THEN 'Global Rule' ELSE CONCAT(l.buildingname, '-', l.floornumber, 'F-', l.roomnumber) END AS locationname",
            "FROM alert_rules ar LEFT JOIN locations l ON l.id = ar.locationid",
            "WHERE ar.locationid IS NULL OR ar.locationid IN",
            "<foreach collection='locationIds' item='locationId' open='(' separator=',' close=')'>",
            "#{locationId}",
            "</foreach>",
            "ORDER BY ar.locationid IS NULL DESC, ar.locationid, ar.sensortype",
            "</script>"
    })
    @Results({
            @Result(column = "rulename", property = "ruleName"),
            @Result(column = "rulecondition", property = "ruleCondition"),
            @Result(column = "sensortype", property = "sensorType"),
            @Result(column = "locationid", property = "locationId"),
            @Result(column = "locationname", property = "locationName"),
            @Result(column = "createdbyuserid", property = "createdByUserId"),
            @Result(column = "createtime", property = "createTime"),
            @Result(column = "updatetime", property = "updateTime")
    })
    List<AlertRule> getRulesForManagementByLocationIds(@Param("locationIds") List<Integer> locationIds);

    @Select("SELECT id, rulename, description, rulecondition, sensortype, severity, locationid, enabled, createdbyuserid, createtime, updatetime " +
            "FROM alert_rules WHERE locationid = #{locationId} AND sensortype = #{sensorType} LIMIT 1")
    @Results({
            @Result(column = "rulename", property = "ruleName"),
            @Result(column = "rulecondition", property = "ruleCondition"),
            @Result(column = "sensortype", property = "sensorType"),
            @Result(column = "locationid", property = "locationId"),
            @Result(column = "createdbyuserid", property = "createdByUserId"),
            @Result(column = "createtime", property = "createTime"),
            @Result(column = "updatetime", property = "updateTime")
    })
    AlertRule getLocationRuleBySensorType(@Param("locationId") Integer locationId, @Param("sensorType") String sensorType);

    @Select("SELECT id, rulename, description, rulecondition, sensortype, severity, locationid, enabled, createdbyuserid, createtime, updatetime " +
            "FROM alert_rules WHERE locationid IS NULL AND sensortype = #{sensorType} LIMIT 1")
    @Results({
            @Result(column = "rulename", property = "ruleName"),
            @Result(column = "rulecondition", property = "ruleCondition"),
            @Result(column = "sensortype", property = "sensorType"),
            @Result(column = "locationid", property = "locationId"),
            @Result(column = "createdbyuserid", property = "createdByUserId"),
            @Result(column = "createtime", property = "createTime"),
            @Result(column = "updatetime", property = "updateTime")
    })
    AlertRule getGlobalRuleBySensorType(@Param("sensorType") String sensorType);

    @Insert("INSERT INTO alert_rules (rulename, description, rulecondition, sensortype, severity, locationid, enabled, createdbyuserid, createtime, updatetime) " +
            "VALUES (#{ruleName}, #{description}, #{ruleCondition}, #{sensorType}, #{severity}, #{locationId}, #{enabled}, #{createdByUserId}, #{createTime}, #{updateTime})")
    int insertRule(AlertRule rule);

    @Update("UPDATE alert_rules SET rulename = #{ruleName}, description = #{description}, rulecondition = #{ruleCondition}, " +
            "severity = #{severity}, enabled = #{enabled}, createdbyuserid = #{createdByUserId}, updatetime = #{updateTime} WHERE id = #{id}")
    int updateRule(AlertRule rule);

    @Delete("DELETE FROM alert_rules WHERE locationid = #{locationId} AND sensortype = #{sensorType}")
    int deleteLocationRuleBySensorType(@Param("locationId") Integer locationId, @Param("sensorType") String sensorType);

    @Delete("DELETE FROM alert_rules WHERE locationid IS NULL AND sensortype = #{sensorType}")
    int deleteGlobalRuleBySensorType(@Param("sensorType") String sensorType);
}
