package scpf.extend.mycat.rule;

import com.sun.org.apache.bcel.internal.generic.RETURN;
import io.mycat.MycatServer;
import io.mycat.cache.CachePool;
import io.mycat.route.function.AbstractPartitionAlgorithm;
import io.mycat.route.sequence.handler.IncrSequenceTimeHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scpf.extend.mycat.db.jdbc.JdbcHelper;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 根据组织分区规则，如果每次分区字段的数据库索引值有变，会清空SQLRouteCache缓存
 */
public class OrganizationPartitionAlgorithm extends AbstractPartitionAlgorithm {

    protected static final Logger LOGGER = LoggerFactory.getLogger(OrganizationPartitionAlgorithm.class);


    private static int DEFAULT_DB = 0;


    private CachePool sqlRouteCache;

    private Map<String, Integer> partitionCacheMap = new HashMap<>();

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

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true){
                    try {
                        initialize();
                        Thread.sleep(10000);
                    }catch(Exception e){
                        e.printStackTrace();
                    }
                }
            }
        });
        thread.start();

    }
    private boolean hadChangePartition(List list,Map<String,Integer> tempMap){
        if(list.size() != tempMap.size()){
            return true;
        }
        for(Object o : list){
            Map map = (Map) o;
            if(null==tempMap.get(map.get("code"))
                    || tempMap.get(map.get("code")).intValue() != Integer.valueOf(map.get("partition").toString()).intValue()){
                return true;
            }
        }
        return false;
    }


    public void initialize(){
        try {
            if(null==sqlRouteCache && null!=MycatServer.getInstance()){
                sqlRouteCache = MycatServer.getInstance().getCacheService().getCachePool("SQLRouteCache");
            }
            StringBuilder sb = new StringBuilder();
            sb.append(" SELECT spmd.field_value `code`,spmd.database_index `partition` FROM scpf_partition_field spf ")
                    .append(" INNER JOIN scpf_partition_match_db spmd ON spmd.partition_field_id = spf.id AND spmd.deleted = 'N' ")
                    .append(" WHERE  spf.deleted = 'N' AND spf.partition_field = 'zone' ");
            List list = JdbcHelper.query(sb.toString());
            Map<String, Integer> tempMap = new HashMap<>();
            tempMap.putAll(partitionCacheMap);
            for(Object o : list){
                Map map = (Map) o;
                partitionCacheMap.put(map.get("code").toString(),Integer.valueOf(map.get("partition").toString()));
            }
            if(hadChangePartition(list,tempMap)){
                sqlRouteCache.clearCache();
                LOGGER.info("清空SQLRouteCache缓存");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
