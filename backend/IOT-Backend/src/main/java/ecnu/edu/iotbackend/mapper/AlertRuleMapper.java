package ecnu.edu.iotbackend.mapper;

import ecnu.edu.iotbackend.entity.AlertRule;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface AlertRuleMapper {

    @Select("SELECT id, rulename, description, rulecondition, sensortype, severity, createtime " +
            "FROM alert_rules ORDER BY id")
    @Results({
        @Result(column = "rulename",      property = "ruleName"),
        @Result(column = "rulecondition", property = "ruleCondition"),
        @Result(column = "sensortype",    property = "sensorType"),
        @Result(column = "createtime",    property = "createTime")
    })
    List<AlertRule> getAllRules();
}
