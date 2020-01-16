package scpf.extend.mycat.rule;

import com.sun.org.apache.bcel.internal.generic.RETURN;
import io.mycat.route.function.AbstractPartitionAlgorithm;
import scpf.extend.mycat.db.jdbc.JdbcHelper;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OrganizationPartitionAlgorithm extends AbstractPartitionAlgorithm {
    private static int DEFAULT_DB = 0;

    private Map<String, Integer> partitionCacheMap = new HashMap<>();;

    /**
     * 实际分片计算规则的函数
     * @param columnValue 传入的数据字段值
     * @return 返回dataNode的标号
     */
    @Override
    public Integer calculate(String columnValue) {
        if(null!=partitionCacheMap.get(columnValue)){
            return partitionCacheMap.get(columnValue);
        }
        return DEFAULT_DB;
    }

    @Override
    public void init() {
        initialize();
    }

    public void initialize(){
        try {
            List list = JdbcHelper.query("SELECT `partition`,`code` FROM scpf_partitions  ");
            for(Object o : list){
                Map map = (Map) o;
                partitionCacheMap.put(map.get("code").toString(),Integer.valueOf(map.get("partition").toString()));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
